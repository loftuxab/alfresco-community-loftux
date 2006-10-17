/*
* Copyright (C) 2005 Alfresco, Inc.
*
* Licensed under the Mozilla Public License version 1.1 
* with a permitted attribution clause. You may obtain a
* copy of the License at
*
*   http://www.alfresco.org/legal/license.txt
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
* either express or implied. See the License for the specific
* language governing permissions and limitations under the
* License.
*/

#ifndef UNICODE
 #define UNICODE
#endif

#include <windows.h>
#include <string>
#include <iostream>
#include <fstream>

using namespace std;

// Registry key constants

#define REGISTRY_SEPERATOR	L"\\"

// Java home path key name

#define JAVA_HOME_KEY	L"JavaHome"

// Java registry key names

const wchar_t* javaKeys[] = { L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_08",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_07",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_06",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_05",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_04",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_03",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_02",
							  L"SOFTWARE\\JavaSoft\\Java Development Kit\\1.5.0_01",
							  NULL
};

// OpenOffice install path key name prefix

#define OPENOFFICE_INSTALLPATH_KEY	L"OpenOffice.org "

// OpenOffice registry key names

const wchar_t* ooKeys[] = { L"SOFTWARE\\OpenOffice.org\\UNO\\InstallPath",
							NULL
};

// Environment variables

#define ENV_JAVA_HOME	L"JAVA_HOME"
#define ENV_OO_PATH		L"OPENOFFICE_PATH"

// Status return codes

#define STS_CREATEDBATFILE	    0
#define STS_JDKNOTFOUND			1
#define STS_OOPATHNOTFOUND		2
#define STS_NOPATHSFOUND        3

// Output batch file name

#define BATCH_FILE_NAME	"SetPaths.bat"

/**
* Main application
*/
int main( int argc, char* argv[]) {

	// Parse any command line argument

	bool verbose = false;

	if ( argc > 0) {

		int idx = 0;

		while ( idx < argc) {
			if ( _stricmp("-v", argv[idx]) == 0)
				verbose = true;
			idx++;
		}
	}

	// Output a startup banner

	if ( verbose)
		wcout << L"Install Paths Utility" << endl;

	// Paths for Java and OpenOffice

	wstring javaPath;
	wstring openOfficePath;

	// Check if the JAVA_HOME environment variable is already set

	wchar_t envBuf[512];
	size_t envLen = sizeof(envBuf);

	_wgetenv_s( &envLen, envBuf, sizeof( envBuf), ENV_JAVA_HOME);

	if ( envLen > 0) {

		// Logging

		if ( verbose)
			wcout << ENV_JAVA_HOME << L" already set, " << envBuf << endl;
		javaPath = envBuf;
	}

	// Search for the Java install path, if not already set

	wchar_t valueBuf[MAX_PATH];
	DWORD valueLen = sizeof ( valueBuf);
	unsigned int idx = 0;

	if ( javaPath.length() == 0) {

		// Check the user specific registry keys

		HKEY javaKey = NULL;

		while ( javaPath.length() == 0 && javaKeys[idx] != NULL ) {

			// Open the key

			if ( RegOpenKeyEx(HKEY_CURRENT_USER, javaKeys[idx], 0, KEY_QUERY_VALUE, &javaKey) == ERROR_SUCCESS) {

				// Check for the Java home path

				valueLen = sizeof ( valueBuf);

				DWORD sts = RegQueryValueEx( javaKey, JAVA_HOME_KEY, 0, NULL, (BYTE*) valueBuf, &valueLen);

				if ( sts == ERROR_SUCCESS) {

					// Set the JDK path

					javaPath = valueBuf;

					// Logging

					if ( verbose)
						wcout << "Java path from HKU\\" << javaKeys[idx] << endl << " = " << javaPath << endl;
				}

				// Close the registry key

				RegCloseKey( javaKey);
			}

			// Update the Java path index

			idx++;
		}

		// Check the global registry keys

		idx = 0;

		while ( javaPath.length() == 0 && javaKeys[idx] != NULL ) {

			// Open the key

			if ( RegOpenKeyEx(HKEY_LOCAL_MACHINE, javaKeys[idx], 0, KEY_QUERY_VALUE, &javaKey) == ERROR_SUCCESS) {

				// Check for the Java home path

				valueLen = sizeof ( valueBuf);

				DWORD sts = RegQueryValueEx( javaKey, JAVA_HOME_KEY, 0, NULL, (BYTE*) valueBuf, &valueLen);
				
				if ( sts == ERROR_SUCCESS) {

					// Set the JDK path

					javaPath = valueBuf;

					// Logging

					if ( verbose)
						wcout << "Java path from HKLM\\" << javaKeys[idx] << endl << " = " << javaPath << endl;
				}

				// Close the registry key

				RegCloseKey( javaKey);
			}

			// Update the Java path index

			idx++;
		}

		// Logging

		if ( verbose && javaPath.length() == 0)
			wcout << "Failed to find Java path" << endl;
	}

	// Search for the OpenOffice install path in the current user key

	HKEY ooKey = NULL;
	idx = 0;

	while ( openOfficePath.length() == 0 && ooKeys[idx] != NULL ) {

		// Open the key

		if ( RegOpenKeyEx(HKEY_CURRENT_USER, ooKeys[idx], 0, KEY_QUERY_VALUE, &ooKey) == ERROR_SUCCESS) {

			// Logging

			if ( verbose)
				wcout << L"Opened key HKU\\" << ooKeys[idx] << endl;

			// Search for an OpenOffice install path

			DWORD sts = ERROR_SUCCESS;

			wchar_t keyName[64];
			DWORD keyNameLen;
			DWORD keyIndex = 0;

			while ( sts == ERROR_SUCCESS && openOfficePath.length() == 0) {

				keyNameLen = sizeof(keyName);
				valueLen = sizeof ( valueBuf);

				sts = RegEnumValue( ooKey, keyIndex++, keyName, &keyNameLen, NULL, NULL, (BYTE*) valueBuf, &valueLen);

				if ( sts != ERROR_SUCCESS)
					continue;

				// Check if the key is the OpenOffice install path key

				if ( keyNameLen > 0 &&
					 wcsncmp(keyName, OPENOFFICE_INSTALLPATH_KEY, wcslen(OPENOFFICE_INSTALLPATH_KEY)) == 0) {

					// Logging

					if ( verbose)
						wcout << " Found key " << keyName << endl;

					// Set the OpenOffice install path

					openOfficePath = valueBuf;

					// Logging

					if ( verbose)
						wcout << "OpenOffice path from HKU\\" << ooKeys[idx] << endl << " = " << openOfficePath << endl;
				 }
			}

			// Close the registry key

			RegCloseKey( ooKey);
		}

		// Update the registry path index

		idx++;
	}

	// Search for the OpenOffice install path in the global keys, if not already set

	idx = 0;

	while ( openOfficePath.length() == 0 && ooKeys[idx] != NULL ) {

		// Open the key

		if ( RegOpenKeyEx(HKEY_LOCAL_MACHINE, ooKeys[idx], 0, KEY_QUERY_VALUE, &ooKey) == ERROR_SUCCESS) {

			// Logging

			if ( verbose)
				wcout << L"Opened key HKLM\\" << ooKeys[idx] << endl;

			// Search for an OpenOffice install path

			DWORD sts = ERROR_SUCCESS;

			wchar_t keyName[64];
			DWORD keyNameLen;
			DWORD keyIndex = 0;

			while ( sts == ERROR_SUCCESS && openOfficePath.length() == 0) {

				keyNameLen = sizeof(keyName);
				valueLen = sizeof ( valueBuf);

				sts = RegEnumValue( ooKey, keyIndex++, keyName, &keyNameLen, NULL, NULL, (BYTE*) valueBuf, &valueLen);

				if ( sts != ERROR_SUCCESS)
					continue;

				// Check if the key is the OpenOffice install path key

				if ( keyNameLen > 0 &&
					wcsncmp(keyName, OPENOFFICE_INSTALLPATH_KEY, wcslen(OPENOFFICE_INSTALLPATH_KEY)) == 0) {

					// Logging

					if ( verbose)
						wcout << " Found key " << keyName << endl;

					// Set the OpenOffice install path

					openOfficePath = valueBuf;

					// Logging

					if ( verbose)
						wcout << "OpenOffice path from HKLM\\" << ooKeys[idx] << endl << " = " << openOfficePath << endl;
				 }
			}

			// Close the registry key

			RegCloseKey( ooKey);
		}

		// Update the registry path index

		idx++;
	}

	// Set the status depending on which paths were found

	unsigned int sts = 0;

	if ( javaPath.length() == 0)
		sts = STS_JDKNOTFOUND;
	if ( openOfficePath.length() == 0)
		sts += STS_OOPATHNOTFOUND;

	// Create a batch file to set the JAVA_HOME and OpenOffice install path environment variables

	wofstream batStream ( BATCH_FILE_NAME, ios_base::out);

	batStream << L"@ECHO OFF" << endl;
	batStream << L"REM Set Paths Utility" << endl;

	if ( javaPath.length() > 0)
		batStream << L"SET " << ENV_JAVA_HOME << L"=" << javaPath << endl;

	if ( openOfficePath.length() > 0)
		batStream << L"SET " << ENV_OO_PATH << L"=" << openOfficePath << endl;
	batStream.close();

	// Return the status

	return sts;
}