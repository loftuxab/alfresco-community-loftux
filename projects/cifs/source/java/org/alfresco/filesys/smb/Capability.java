package org.alfresco.filesys.smb;

/**
 * SMB Capabilities Class
 * 
 * <p>Contains the capability flags for the client/server during a session setup.
 */
public class Capability
{
	//	Capabilities
	
	public static final int RawMode						= 0x00000001;
	public static final int MpxMode						= 0x00000002;
	public static final int Unicode						= 0x00000004;
	public static final int LargeFiles				= 0x00000008;
	public static final int NTSMBs						= 0x00000010;
	public static final int RemoteAPIs				= 0x00000020;
	public static final int NTStatus					= 0x00000040;
	public static final int Level2Oplocks			= 0x00000080;
	public static final int LockAndRead				= 0x00000100;
	public static final int NTFind						= 0x00000200;
	public static final int DFS								= 0x00001000;
	public static final int InfoPassthru			= 0x00002000;
	public static final int LargeRead 				= 0x00004000;
	public static final int LargeWrite				= 0x00008000;
	public static final int UnixExtensions		= 0x00800000;
	public static final int BulkTransfer			= 0x20000000;
	public static final int CompressedData		=	0x40000000;
	public static final int ExtendedSecurity	=	0x80000000;
}
