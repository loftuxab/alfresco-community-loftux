/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_cloud.misc;

import org.alfresco.repo.action.executer.MailActionExecuterTest;
import org.junit.Ignore;

/**
 * This class is only here to trigger test execution in the Cloud build for {@link MailActionExecuterTest}.
 */
@Ignore("Temporary disabling for headqa_cluster branch.")
public class CloudMailActionExecuterTest extends MailActionExecuterTest
{
    // Intentionally empty. This class inherits everything from its superclass.
    //
    // FIXME There should be no need for this class to exist - it should be possible to rerun
    // the enterprise MailActionExecuterTest in a cloud build/config without an additional test class like this. :(
}
