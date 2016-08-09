#ifndef VIDEO_DATA_H
#define VIDEO_DATA_H

#define FRAME_SZIE 409600

struct VideoData
{
	VideoData(unsigned int size, unsigned char *buffer);
	unsigned char data[FRAME_SZIE];
	unsigned int length;
};

#endif
