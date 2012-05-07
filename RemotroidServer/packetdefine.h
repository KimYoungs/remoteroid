#pragma once

#define PORT				50000
#define MAXSIZE				4096
#define HEADERSIZE			6
#define OPCODESIZE			2
#define TOTALSIZE			4
#define FILENAMESIZE		100
#define FILESIZESIZE		100
#define JPGSIZELEGNTH		10


#define OP_SENDFILEINFO		1
#define OP_SENDFILEDATA		2
#define OP_SENDJPGINFO		3
#define OP_SENDJPGDATA		4



#define WM_RECVJPGINFO	WM_USER+100
#define WM_RECVJPGDATA	WM_USER+101