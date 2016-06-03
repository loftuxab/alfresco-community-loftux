/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.share.util;

import java.io.IOException;
import java.util.Date;
import java.util.Random;

import sun.misc.BASE64Encoder;
import sun.misc.BASE64Decoder;

/**
 * Methods that encrypt and decrypt password for security reasons
 * 
 * @author Oana.Caciuc
 */

public class EncryptDecryptPassword {

	private static Random rand = new Random((new Date()).getTime());

	/**
	 * 
	 * @param str
	 * @return encrypted password
	 */

	public static String encrypt(String str) {

		BASE64Encoder encoder = new BASE64Encoder();

		byte[] salt = new byte[8];

		rand.nextBytes(salt);

		return encoder.encode(salt) + encoder.encode(str.getBytes());
	}

	/**
	 * 
	 * @param encstr
	 *            password
	 * @return decrypted password
	 */

	public static String decrypt(String encstr) {

		if (encstr.length() > 12) {

			String cipher = encstr.substring(12);

			BASE64Decoder decoder = new BASE64Decoder();

			try {

				return new String(decoder.decodeBuffer(cipher));

			} catch (IOException e) {

				// throw new InvalidImplementationException(

				// Fail

			}

		}

		return null;
	}
}
