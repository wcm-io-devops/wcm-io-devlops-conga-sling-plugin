/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.plugins.sling;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;

import org.apache.felix.cm.file.ConfigurationHandler;
import org.apache.sling.provisioning.model.Model;
import org.slf4j.Logger;

/**
 * Transforms a Sling Provisioning file into OSGi configurations (ignoring all other provisioning contents).
 */
public class ProvisioningOsgiConfigPostProcessor implements PostProcessorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "sling-provisioning-osgiconfig";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, PostProcessorContext context) {
    return ProvisioningUtil.isProvisioningFile(context.getFile(), context.getCharset());
  }

  @Override
  public List<FileContext> apply(FileContext fileContext, PostProcessorContext context) {
    File file = fileContext.getFile();
    String charset = fileContext.getCharset();
    Logger logger = context.getLogger();

    try {
      // generate OSGi configurations
      Model model = ProvisioningUtil.getModel(file, charset);
      generateOsgiConfigurations(model, file.getParentFile(), logger);

      // delete provisioning file after transformation
      file.delete();
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to post-process sling provisioning OSGi configurations.", ex);
    }
  }

  /**
   * Generate OSGi configuration for all feature and run modes.
   * @param model Provisioning Model
   * @param dir Target directory
   * @param logger Logger
   * @throws IOException
   */
  private void generateOsgiConfigurations(Model model, File dir, Logger logger) throws IOException {
    ProvisioningUtil.visitOsgiConfigurations(model, new ConfigConsumer() {
      @Override
      public void accept(String path, Dictionary<String, Object> properties) throws IOException {
        logger.info("  Generate " + path);

        File confFile = new File(dir, path);
        confFile.getParentFile().mkdirs();
        try (FileOutputStream os = new FileOutputStream(confFile)) {
          ConfigurationHandler.write(os, properties);
        }
      }
    });
  }

}
