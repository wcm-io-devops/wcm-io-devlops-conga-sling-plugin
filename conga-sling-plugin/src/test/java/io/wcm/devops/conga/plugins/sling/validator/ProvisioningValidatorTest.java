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
package io.wcm.devops.conga.plugins.sling.validator;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import io.wcm.devops.conga.generator.spi.ValidationException;
import io.wcm.devops.conga.generator.spi.ValidatorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.util.PluginManager;

import java.io.File;

import org.apache.commons.lang3.CharEncoding;
import org.junit.Before;
import org.junit.Test;

public class ProvisioningValidatorTest {

  private ValidatorPlugin underTest;

  @Before
  public void setUp() {
    underTest = new PluginManager().get(ProvisioningValidator.NAME, ValidatorPlugin.class);
  }

  @Test
  public void testValid() throws Exception {
    File file = new File(getClass().getResource("/validProvisioning.txt").toURI());
    FileContext fileContext = new FileContext().file(file).charset(CharEncoding.UTF_8);
    assertTrue(underTest.accepts(fileContext, null));
    underTest.apply(fileContext, null);
  }

  @Test(expected = ValidationException.class)
  public void testInvalid() throws Exception {
    File file = new File(getClass().getResource("/invalidProvisioning.txt").toURI());
    FileContext fileContext = new FileContext().file(file).charset(CharEncoding.UTF_8);
    assertTrue(underTest.accepts(fileContext, null));
    underTest.apply(fileContext, null);
  }

  @Test
  public void testInvalidFileExtension() throws Exception {
    File file = new File(getClass().getResource("/noProvisioning.txt").toURI());
    FileContext fileContext = new FileContext().file(file).charset(CharEncoding.UTF_8);
    assertFalse(underTest.accepts(fileContext, null));
  }

}