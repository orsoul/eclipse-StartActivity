#include <stdio.h>
#include <stdlib.h>
#include <linux/input.h>
#include <string.h>
#include <jni.h>
#include <fcntl.h> /*包括文件操作，如open() read() close()write()等*/

#include <stdint.h>
#include <unistd.h>
#include <getopt.h>
#include <sys/ioctl.h>
#include <linux/types.h>
#include <termios.h>
#include <linux/i2c.h>

#include <linux/types.h>
#include <linux/i2c.h>
//#include <linux/i2c-dev.h>
#include <linux/rtc.h>
#include <stdio.h>
#include <time.h>
#include <stdlib.h>
#include <fcntl.h>
#include <unistd.h>
#include <sys/types.h>
#include <sys/ioctl.h>
#include <errno.h>
#include <string.h>

#define I2C_DEFAULT_TIMEOUT		1
#define I2C_DEFAULT_RETRY		3

#define SPI_CPHA		0x01
#define SPI_CPOL		0x02

#define SPI_MODE_0		(0|0)
#define SPI_MODE_1		(0|SPI_CPHA)
#define SPI_MODE_2		(SPI_CPOL|0)
#define SPI_MODE_3		(SPI_CPOL|SPI_CPHA)

#define SPI_CS_HIGH		0x04
#define SPI_LSB_FIRST		0x08
#define SPI_3WIRE		0x10
#define SPI_LOOP		0x20
#define SPI_NO_CS		0x40
#define SPI_READY		0x80
#define SPI_IOC_MAGIC			'k'
struct spi_ioc_transfer {
	__u64		tx_buf;  //用于其他数据buffer，如图片类
	__u64		rx_buf;

	__u32		len;//buffer长度，即多少字节
	__u32		speed_hz;

	__u16		delay_usecs;   //传输延时
	__u8		bits_per_word; //传输位数
	__u8		cs_change;  //SPI通道选择，本OLED模块固定选0

	__u8		XSpos;  //x起始地址
	__u8		XCpos;  //x结束地址
	__u8		YSpos;  //y起始地址
	__u8		YCpos;  //y结束地址

	__u32		Color;  //控制颜色
	__u64	Charfont; //字库数组地址
		__u8 	CharWidth;//文字宽
		__u8	CharHeight;//文字高
		__u8 	Words; //文字数目
		__u8 	Status;       //The screen switch state, such as: 1, close to 0
		__u32  	BackColor; //文字背景颜色
		__u32	TextColor; //文字颜色
		__u16	CharSize;  //整个字所占字节数
};

struct i2c_rdwr_ioctl_data {
	struct i2c_msg __user *msgs;	/* pointers to i2c_msgs */
	__u32 nmsgs;			/* number of i2c_msgs */
};

#define uint32 unsigned long
//typedef unsigned long  uint32;
#define BYTE unsigned char
unsigned char LoopCountCxj=0;
BYTE L[32],R[32];
BYTE TmpE[48],Result[32];
BYTE KeyBuff[64];

BYTE const IPConvert[]=
{
    58,50,42,34,26,18,10, 2,60,52,44,36,28,20,12, 4,
    62,54,46,38,30,22,14, 6,64,56,48,40,32,24,16, 8,
    57,49,41,33,25,17, 9, 1,59,51,43,35,27,19,11, 3,
    61,53,45,37,29,21,13, 5,63,55,47,39,31,23,15, 7
};


BYTE const IP1Convert[]=
{
    40, 8,48,16,56,24,64,32,39, 7,47,15,55,23,63,31,
    38, 6,46,14,54,22,62,30,37, 5,45,13,53,21,61,29,
    36, 4,44,12,52,20,60,28,35, 3,43,11,51,19,59,27,
    34, 2,42,10,50,18,58,26,33, 1,41, 9,49,17,57,25
};
BYTE const PConvert[]=
{
    16, 7,20,21,29,12,28,17, 1,15,23,26, 5,18,31,10,
    2, 8,24,14,32,27, 3, 9,19,13,30, 6,22,11, 4,25
};
BYTE const KeyTable1[]=
{
    57,49,41,33,25,17, 9, 1,58,50,42,34,26,18,10, 2,
    59,51,43,35,27,19,11, 3,60,52,44,36,63,55,47,39,
    31,23,15, 7,62,54,46,38,30,22,14, 6,61,53,45,37,
    29,21,13, 5,28,20,12, 4
};
BYTE const KeyTable2[]=
{
    14,17,11,24, 1, 5, 3,28,15, 6,21,10,23,19,12, 4,
    26, 8,16, 7,27,20,13, 2,41,52,31,37,47,55,30,40,
    51,45,33,48,44,49,39,56,34,53,46,42,50,36,29,32
};
BYTE const KeyLoopTbl[]=
{
    1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1
};
BYTE const EConvert[]=
{
    32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9,10,11,
    12,13,12,13,14,15,16,17,16,17,18,19,20,21,20,21,
    22,23,24,25,24,25,26,27,28,29,28,29,30,31,32, 1
};
BYTE const SConvert[][64]=
{
    {14, 4,13, 1, 2,15,11, 8, 3,10, 6,12,5, 9, 0, 7,
     0,15, 7, 4,14, 2,13, 1,10, 6,12,11, 9, 5, 3, 8,
     4, 1,14, 8,13, 6, 2,11,15,12, 9, 7, 3,10, 5, 0,
     15,12, 8, 2, 4, 9, 1, 7, 5,11, 3,14,10, 0, 6,13},
     {15, 1, 8,14, 6,11, 3, 4, 9, 7, 2,13,12, 0, 5,10,
     3,13, 4, 7,15, 2, 8,14,12, 0, 1,10, 6, 9,11, 5,
     0,14, 7,11,10, 4,13, 1, 5, 8,12, 6, 9, 3, 2,15,
     13, 8,10, 1, 3,15, 4, 2,11, 6, 7,12, 0, 5,14, 9},
     {10, 0, 9,14, 6, 3,15, 5, 1,13,12, 7,11, 4, 2, 8,
     13, 7, 0, 9, 3, 4, 6,10, 2, 8, 5,14,12,11,15, 1,
     13, 6, 4, 9, 8,15, 3, 0,11, 1, 2,12, 5,10,14, 7,
     1,10,13, 0, 6, 9, 8, 7, 4,15,14, 3,11, 5, 2,12},
     {7,13,14, 3, 0, 6, 9,10, 1, 2, 8, 5,11,12, 4,15,
     13, 8,11, 5, 6,15, 0, 3, 4, 7, 2,12, 1,10,14, 9,
     10, 6, 9, 0,12,11, 7,13,15, 1, 3,14, 5, 2, 8, 4,
     3,15, 0, 6,10, 1,13, 8, 9, 4, 5,11,12, 7, 2,14},
     {2,12, 4, 1, 7,10,11, 6, 8, 5, 3,15,13, 0,14, 9,
     14,11, 2,12, 4, 7,13, 1, 5, 0,15,10, 3, 9, 8, 6,
     4, 2, 1,11,10,13, 7, 8,15, 9,12, 5, 6, 3, 0,14,
     11, 8,12, 7, 1,14, 2,13, 6,15, 0, 9,10, 4, 5, 3},
     {12, 1,10,15, 9, 2, 6, 8, 0,13, 3, 4,14, 7, 5,11,
     10,15, 4, 2, 7,12, 9, 5, 6, 1,13,14, 0,11, 3, 8,
     9,14,15, 5, 2, 8,12, 3, 7, 0, 4,10, 1,13,11, 6,
     4, 3, 2,12, 9, 5,15,10,11,14, 1, 7, 6, 0, 8,13},
     {4,11, 2,14,15, 0, 8,13, 3,12, 9, 7, 5,10, 6, 1,
     13, 0,11, 7, 4, 9, 1,10,14, 3, 5,12, 2,15, 8, 6,
     1, 4,11,13,12, 3, 7,14,10,15, 6, 8, 0, 5, 9, 2,
     6,11,13, 8, 1, 4,10, 7, 9, 5, 0,15,14, 2, 3,12},
     {13, 2, 8, 4, 6,15,11, 1,10, 9, 3,14, 5, 0,12, 7,
     1,15,13, 8,10, 3, 7, 4,12, 5, 6,11, 0,14, 9, 2,
     7,11, 4, 1, 9,12,14, 2, 0, 6,10,13,15, 3, 5, 8,
     2, 1,14, 7, 4,10, 8,13,15,12, 9, 0, 3, 5, 6,11}
};

/* not all platforms use <asm-generic/ioctl.h> or _IOC_TYPECHECK() ... */
#define SPI_MSGSIZE(N) \
	((((N)*(sizeof (struct spi_ioc_transfer))) < (1 << _IOC_SIZEBITS)) \
		? ((N)*(sizeof (struct spi_ioc_transfer))) : 0)
#define SPI_IOC_MESSAGE(N) _IOW(SPI_IOC_MAGIC, 0, char[SPI_MSGSIZE(N)])


/* Read / Write of SPI mode (SPI_MODE_0..SPI_MODE_3) */
#define SPI_IOC_RD_MODE			_IOR(SPI_IOC_MAGIC, 1, __u8)
#define SPI_IOC_WR_MODE			_IOW(SPI_IOC_MAGIC, 1, __u8)

/* Read / Write SPI bit justification */
#define SPI_IOC_RD_LSB_FIRST		_IOR(SPI_IOC_MAGIC, 2, __u8)
#define SPI_IOC_WR_LSB_FIRST		_IOW(SPI_IOC_MAGIC, 2, __u8)

/* Read / Write SPI device word length (1..N) */
#define SPI_IOC_RD_BITS_PER_WORD	_IOR(SPI_IOC_MAGIC, 3, __u8)
#define SPI_IOC_WR_BITS_PER_WORD	_IOW(SPI_IOC_MAGIC, 3, __u8)

/* Read / Write SPI device default max speed hz */
#define SPI_IOC_RD_MAX_SPEED_HZ		_IOR(SPI_IOC_MAGIC, 4, __u32)
#define SPI_IOC_WR_MAX_SPEED_HZ		_IOW(SPI_IOC_MAGIC, 4, __u32)

#define SPI_IOC_OLED_INIT		_IOR(SPI_IOC_MAGIC, 5, __u32)

/* set oled cursor postion*/
#define SPI_IOC_DRAW_LINE 		_IOR(SPI_IOC_MAGIC, 6, __u32)
#define SPI_IOC_DRAW_BMP	_IOR(SPI_IOC_MAGIC, 14, __u32)
/* set oled  char font*/
#define SPI_IOC_COPY_RECT		_IOR(SPI_IOC_MAGIC, 7, __u32)

/* clear screen buffer*/
#define SPI_IOC_CLEAR_SCREEN 	_IOR(SPI_IOC_MAGIC, 8, __u32)

/* open oled screen*/
#define SPI_IOC_SCREEN_SLEEP 	_IOR(SPI_IOC_MAGIC, 9, __u32)

/* send string*/
#define SPI_IOC_SEND_STRING 	_IOR(SPI_IOC_MAGIC, 10, __u32)

/* OLED_HorizontalScrollSetup*/
#define SPI_IOC_HORIZONTAL_SCROLL _IOR(SPI_IOC_MAGIC, 11, __u32)

#define SPI_IOC_DRAW_RECT 	_IOR(SPI_IOC_MAGIC, 12, __u32)
/* set oled  color*/
#define SPI_IOC_FILL_SCREEN 		_IOR(SPI_IOC_MAGIC, 13, __u32)
#define ARRAY_SIZE(a) (sizeof(a) / sizeof((a)[0]))

/* copy a rect to new address*/
#define SPI_IOC_COPY_RECT 		_IOR(SPI_IOC_MAGIC, 7, __u32)

int fd_gpio;
int fd_spi;
int fd;
int fd_oled;
static uint8_t mode;
static uint8_t bits = 8;
static uint32_t speed = 1000000;
static uint16_t delay;

jstring
Java_com_hardware_Hardware_stringFromJNI( JNIEnv* env,
                                                  jobject thiz )
{
    return (*env)->NewStringUTF(env, "keys from JNI create by xu !");
}

jint Java_com_hardware_Hardware_openSPI( JNIEnv* env,
        jobject thiz )
{
		int ret = 0;
	fd_spi = open("/dev/spidev0.0", O_RDWR);
	
	if (ioctl(fd_spi, SPI_IOC_WR_MODE, &mode) == -1)
		ret =-2;

	if (ioctl(fd_spi, SPI_IOC_RD_MODE, &mode) == -1)
		ret =-3;

	if (ioctl(fd_spi, SPI_IOC_WR_BITS_PER_WORD, &bits) == -1)
		ret =-4;

	if (ioctl(fd_spi, SPI_IOC_RD_BITS_PER_WORD, &bits) == -1)
		ret =-5;

	if (ioctl(fd_spi, SPI_IOC_WR_MAX_SPEED_HZ, &speed) == -1)
		ret =-6;

	if (ioctl(fd_spi, SPI_IOC_RD_MAX_SPEED_HZ, &speed) == -1)
		ret =-7;

	if (ioctl(fd_spi, SPI_IOC_OLED_INIT, NULL) == -1)
			ret =-15;

	return (ret == 0 ? fd_spi : ret);
}

jint Java_com_hardware_Hardware_transfer( JNIEnv* env,
        jobject thiz,jint fd_spi, jchar len, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos, jchar Color, jchar CharWidth,
    	jchar CharHeight, jchar BackColor, jchar TextColor, jbyte *buffer)
//jint Java_com_hardware_Hardware_transfer( JNIEnv* env,
//        jobject thiz,jint fd_spi,jint cmd, jbyte *buffer)
{
	int ret;
	struct spi_ioc_transfer tr1 = {
		.XSpos = 0,
		.XCpos = 159,
		.YSpos = 70,
		.YCpos = 100,
		.Color = Color,
		.Words = 10,
		.Status = 80,

		.BackColor = 63,
		.TextColor = TextColor,
	};
		struct spi_ioc_transfer tr2 = {
		.XSpos = 0,
		.XCpos = 160,
		.YSpos = 0,
		.YCpos = 128,
		.Color = Color,

		.BackColor = 63,
		.TextColor = TextColor,
	};

	struct spi_ioc_transfer tr = {
		.len = len,
		//.len = 1,
		.delay_usecs = delay,
		.speed_hz = speed,
		.bits_per_word = bits,
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
		.Color = Color,
		.Charfont = (unsigned long)buffer+16,
		.CharWidth = CharWidth,
	 	.CharHeight =CharHeight,
		.BackColor = BackColor,
		.TextColor = TextColor,
		.CharSize = (((CharHeight)/8) * CharHeight),
		.Words = (len / (((CharHeight)/8) * CharHeight)),
	};

	ret = ioctl(fd_spi, SPI_IOC_CLEAR_SCREEN, &tr2);
	//ret = ioctl(fd_spi, SPI_IOC_DRAW_RECT, &tr1);
	//ret = ioctl(fd_spi, SPI_IOC_DRAW_LINE, &tr1);
	//ret = ioctl(fd_spi, SPI_IOC_FILL_SCREEN, &tr1);
	ret = ioctl(fd_spi, SPI_IOC_HORIZONTAL_SCROLL, &tr);
	//ret = ioctl(fd_spi, SPI_IOC_COPY_RECT, &tr1);
	ret = ioctl(fd_spi, SPI_IOC_SEND_STRING, &tr);

	return 0;
}

jint Java_com_hardware_Hardware_transferString( JNIEnv* env,
        jobject thiz,jint fd_spi, jchar len, jchar XSpos,
		jchar YSpos, jchar CharWidth, jchar CharHeight,
		jlong BackColor, jlong TextColor, jbyte *buffer)
{
	struct spi_ioc_transfer tr = {
		.XSpos = XSpos,
		.YSpos = YSpos,
		.Charfont = (unsigned long)buffer+16,
		.CharWidth = CharWidth,
	 	.CharHeight =CharHeight,
		.BackColor = BackColor,
		.TextColor = TextColor,
		.CharSize = (((CharHeight)/8) * CharHeight),
		.Words = (len / (((CharHeight)/8) * CharHeight)),
	};

	return ioctl(fd_spi, SPI_IOC_SEND_STRING, &tr);
}


jint Java_com_hardware_Hardware_drawLine(JNIEnv* env,
		jobject thiz, jint fd_spi, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos, jlong LineColor)
{
	struct spi_ioc_transfer tr = {
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
		.Color = LineColor,
	};

	return ioctl(fd_spi, SPI_IOC_DRAW_LINE, &tr);
}
jint Java_com_hardware_Hardware_drawBMP(JNIEnv* env,
		jobject thiz, jint fd_spi, jint len, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos, jbyte *buffer)
{
	struct spi_ioc_transfer tr = {
		.len = len,
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
		.tx_buf = (unsigned long)buffer+16,
	};

	return ioctl(fd_spi, SPI_IOC_DRAW_BMP, &tr);
}

jint Java_com_hardware_Hardware_drawRect(JNIEnv* env,
		jobject thiz, jint fd_spi, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos, jlong LineColor, jlong FillColor)
{
	struct spi_ioc_transfer tr = {
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
		.BackColor = FillColor,
		.TextColor = LineColor,
	};

	return ioctl(fd_spi, SPI_IOC_DRAW_RECT, &tr);
}

jint Java_com_hardware_Hardware_copyRect(JNIEnv* env,
		jobject thiz, jint fd_spi, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos, jchar NewXSpos, jchar NewYSpos)
{
	struct spi_ioc_transfer tr = {
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
		.CharWidth = NewXSpos,
	 	.CharHeight =NewYSpos,
	};

	return ioctl(fd_spi, SPI_IOC_COPY_RECT, &tr);
}

jint Java_com_hardware_Hardware_fillScreen(JNIEnv* env,
		jobject thiz, jint fd_spi, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos, jlong FillColor)
{
	struct spi_ioc_transfer tr = {
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
		.Color = FillColor,
	};

	return ioctl(fd_spi, SPI_IOC_FILL_SCREEN, &tr);
}

jint Java_com_hardware_Hardware_clearScreen(JNIEnv* env,
		jobject thiz, jint fd_spi, jchar XSpos, jchar XCpos,
    	jchar YSpos,jchar YCpos)
{
	struct spi_ioc_transfer tr = {
		.XSpos = XSpos,
		.XCpos = XCpos,
		.YSpos = YSpos,
		.YCpos = YCpos,
	};

	return ioctl(fd_spi, SPI_IOC_CLEAR_SCREEN, &tr);
}

jint Java_com_hardware_Hardware_sleep( JNIEnv* env,
        jobject thiz,jint fd_spi,jint cmd )
{
	int ret;
	struct spi_ioc_transfer tr = {
				.Status = cmd,
			};
	ret = ioctl(fd_spi, SPI_IOC_SCREEN_SLEEP, &tr);

	return ret;
}


jint Java_com_hardware_Hardware_closeSPI( JNIEnv* env,
        jobject thiz )
{
	close(fd_spi);

	return 1;
}

jint Java_com_hardware_Hardware_usleep( JNIEnv* env,
        jobject thiz ,jint us)
{
	usleep(us);
    return 0;
}

jint Java_com_hardware_Hardware_reset( JNIEnv* env,
        jobject thiz )
{
	ioctl(fd_gpio,0, 20);
	usleep(1000);
	ioctl(fd_gpio,1, 20);
	usleep(1000);
	ioctl(fd_gpio,0, 20);
	usleep(1000);
	ioctl(fd_gpio,1, 20);
	usleep(50000);//5ms
	return ioctl(fd_gpio,0, 20);
}

jint Java_com_hardware_Hardware_openGPIO( JNIEnv* env,
        jobject thiz )
{
	fd_gpio = open("/dev/gpio", O_RDWR);
	return fd_gpio;
}

jint Java_com_hardware_Hardware_setGPIO( JNIEnv* env,
        jobject thiz ,jint cmd ,jint arg )
{
	return ioctl(fd_gpio,cmd, arg);
}

jint Java_com_hardware_Hardware_closeGPIO( JNIEnv* env,
        jobject thiz )
{
	close(fd_gpio);
	return 1;
}

//char serial[][21] = {"/dev/s3c2410_serial0","/dev/s3c2410_serial1","/dev/s3c2410_serial2","/dev/s3c2410_serial3"};

jint Java_com_hardware_Hardware_openSerialPort( JNIEnv* env,
        jobject thiz , jstring devName, jlong baud, jint dataBits, jint stopBits)
{

	const char *str = (*env)->GetStringUTFChars(env,devName,NULL);
	fd = open(str, O_RDWR);
	(*env)->ReleaseStringUTFChars(env, devName, str);
	if(fd == -1)
	return fd;

	int speed_arr[] = { B9600, B19200, B38400, B57600, B115200, B115200, B115200 ,B1152000};
	struct termios attr;
	int speed_num;

	tcgetattr(fd, &attr);
	speed_num = (baud/19200) > 6 ? 7 : (baud/19200);
	cfsetispeed(&attr, speed_arr[speed_num]);
    cfsetospeed(&attr, speed_arr[speed_num]);

    attr.c_iflag = 0;
    attr.c_oflag = 0;
    attr.c_lflag = 0;

    attr.c_cc[VTIME] = 0;
    attr.c_cc[VMIN] = 0;
    attr.c_cflag |= CLOCAL|CREAD;
    attr.c_cflag &= ~CSIZE;
    attr.c_cflag |= CS8;
    attr.c_cflag &= ~PARENB;  //无奇偶校验位
    attr.c_cflag &= ~CSTOPB;

    tcsetattr(fd, TCSANOW, &attr);

    return fd;
}

jint Java_com_hardware_Hardware_read( JNIEnv* env,
        jobject thiz ,jint fd, jbyteArray buf, jint len )
{
	jbyte * str = (*env)->GetByteArrayElements(env,buf,NULL);
	int num = read(fd,str,len);
	(*env)->ReleaseByteArrayElements(env,buf, str, 0);
	return num;
}

jint Java_com_hardware_Hardware_write( JNIEnv* env,
        jobject thiz ,jint fd, jbyteArray data)
{
	jbyte * str = (*env)->GetByteArrayElements(env,data,NULL);
	jint length = (*env)->GetArrayLength(env,data);
	int num = write(fd,str,length);
	(*env)->ReleaseByteArrayElements(env,data, str, 0);
	return num;
}

jint Java_com_hardware_Hardware_select( JNIEnv* env,
        jobject thiz ,jint fd, jint sec,jint usec)
{
        fd_set fds;
        struct timeval timeout={sec,usec};
        FD_ZERO(&fds);
        FD_SET(fd, &fds);
        return select(fd+1,&fds,NULL,NULL,&timeout);
}

jint Java_com_hardware_Hardware_close( JNIEnv* env,
        jobject thiz ,jint fd)
{
	close(fd);
	return 1;
}

void KeyConvert1Prog(BYTE *BinArray)
{
    BYTE i;
    BYTE TmpArray[56];

    for(i=0;i<56;i++)
    {
        TmpArray[i]=BinArray[KeyTable1[i]-1];
    }
    for(i=0;i<56;i++)
    {
        BinArray[i]=TmpArray[i];
    }
}

void KeyRLoopProg(BYTE *BinArray,BYTE Times)
{
    BYTE i,j,k;
    k=KeyLoopTbl[Times];
    for(;k>0;k--)
    {
        j=BinArray[27];
        for(i=27;i>0;i--)
        {
            BinArray[i]=BinArray[i-1];
        }
        BinArray[0]=j;

        j=BinArray[55];
        for(i=55;i>28;i--)
        {
            BinArray[i]=BinArray[i-1];
        }
        BinArray[28]=j;
    }
}

void KeyLLoopProg(BYTE *BinArray,BYTE Times)
{
    BYTE i,j,k;

    BYTE tmp[64];
    for(i=0;i<64;i++)
        tmp[i]=BinArray[i];

    k=KeyLoopTbl[Times];
    for(;k>0;k--)
    {
        j=BinArray[0];
        for(i=0;i<27;i++)
        {
            BinArray[i]=BinArray[i+1];
        }
        BinArray[27]=j;

        j=BinArray[28];
        for(i=28;i<55;i++)
        {
            BinArray[i]=BinArray[i+1];
        }
        BinArray[55]=j;
    }
}

void KeyConvert2Prog(BYTE *BinArray)
{
    BYTE i;
    BYTE TmpArray[48];

    for(i=0;i<48;i++)
    {
        TmpArray[i]=BinArray[KeyTable2[i]-1];
    }
    for(i=0;i<48;i++)
    {
        BinArray[i]=TmpArray[i];
    }
}
//---------------------------------------------------------------------------

void IPConvertProg(BYTE *BinArray)
{
    BYTE i;
    BYTE TmpArray[64];
    for(i=0;i<64;i++)
    {
        TmpArray[i]=BinArray[IPConvert[i]-1];
    }
    for(i=0;i<32;i++)
    {
        L[i]=TmpArray[i];
        R[i]=TmpArray[i+32];
    }
}

void EConvertProg(BYTE *Buffer)
{
    BYTE i;
    for(i=0;i<48;i++)
    {
        TmpE[i]=Buffer[EConvert[i]-1];
    }
}


void CombinProg(BYTE *Source,BYTE *Dest,BYTE Len) //
{
    BYTE i;
    for(i=0;i<Len;i++)
        TmpE[i]=Source[i]^Dest[i];
}

void HexToBinary(BYTE Value,BYTE *BinArray,BYTE Len) //
{
    BYTE i,x;

    for(i=0;i<Len;i++)
    {
        x=Value&(1<<i);
        if(x==0)
            BinArray[i]=0;
        else
            BinArray[i]=1;
    }
}

void SConvertProg()
{
    BYTE Pos[8][2],i,j,Value[8];

    for(i=0;i<8;i++)
    {
        j=i*6;
        Pos[i][0]=TmpE[j]<<1+TmpE[j+5];
        Pos[i][1]=((TmpE[j+1]<<3)&0x0f)+((TmpE[j+2]<<2)&0x0f)+((TmpE[j+3]<<1)&0x0f)+TmpE[j+4];
        Value[i]=SConvert[Pos[i][0]][Pos[i][1]];
        HexToBinary(Value[i],Result+(i<<2),4);
    }
}

void PConvertProg(BYTE *BinArray)
{
    BYTE i;
    BYTE TmpArray[32];

    for(i=0;i<32;i++)
    {
        TmpArray[i]=BinArray[PConvert[i]-1];
    }
    for(i=0;i<32;i++)
    {
        BinArray[i]=TmpArray[i];
    }
}

void IP1ConvertProg(BYTE *BinArray)
{
    BYTE i;
    BYTE TmpArray[64];

    for(i=0;i<64;i++)
    {
        TmpArray[i]=BinArray[IP1Convert[i]-1];
    }
    for(i=0;i<64;i++)
    {
        BinArray[i]=TmpArray[i];
    }
}

void Int64ToBinary(uint32 ValueL,uint32 ValueH,BYTE *BinArray)
{
    BYTE i;
    uint32 x;

    for(i=0;i<32;i++)
    {
        x=ValueL&((uint32)1<<i);
        if(x==0)
            BinArray[i]=0;
        else
            BinArray[i]=1;
    }
    for(i=32;i<64;i++)
    {
        x=ValueH&((uint32)1<<(i-32));
        if(x==0)
            BinArray[i]=0;
        else
            BinArray[i]=1;
    }
}

void BinaryToHex(BYTE *BinArray,uint32 *ValueL,uint32 *ValueH)
{
    BYTE i;

    *ValueL=0;
    *ValueH=0;
    for(i=0;i<32;i++)
    {
        if(BinArray[i]==1)
            *ValueL|=(uint32)1<<i;
    }
    for(i=32;i<64;i++)
    {
        if(BinArray[i]==1)
            *ValueH|=(uint32)1<<(i-32);
    }
}
//----------------------------------------------------

void  StringToBinary(BYTE *s,BYTE *BinArray,int SLen)
{
    BYTE i,j;

    for(i=0;i<SLen;i++)
    {
        j=s[i];
        HexToBinary(j,BinArray+(i<<3),8);
    }
    i<<=3;
    for(;i<64;i++)
        BinArray[i]=0;
//  if(i<8)
//  memset(BinArray+(i<<3),0,64-(i<<3));
}

void EDInt(uint32 *OriValueL,uint32 *OriValueH,uint32 KeyValueL,uint32 KeyValueH,BYTE Mode)
{
    BYTE i,j;
    BYTE BinArray[65];

    Int64ToBinary(*OriValueL,*OriValueH,BinArray);
    IPConvertProg(BinArray);
    for(i=0;i<16;i++)
    {
        Int64ToBinary(KeyValueL,KeyValueH,KeyBuff);
        KeyConvert1Prog(KeyBuff);
        if(Mode==0)
            KeyLLoopProg(KeyBuff,i);
        else
            KeyLLoopProg(KeyBuff,15-i);

        KeyConvert2Prog(KeyBuff);

        EConvertProg(R);

        CombinProg(TmpE,KeyBuff,48);

        SConvertProg();
        PConvertProg(Result);
        CombinProg(L,Result,32);
        for(j=0;j<32;j++)
        {
            L[j]=R[j];
            R[j]=TmpE[j];
        }
    }
    for(i=0;i<32;i++)
    {
        BinArray[i]=R[i];
        BinArray[i+32]=L[i];
    }
    IP1ConvertProg(BinArray);
    BinaryToHex(BinArray,OriValueL,OriValueH);
}

void EDStr(BYTE *Source,BYTE *KeyStr,int SLen,int KeyLen,BYTE *ResultStr)
{
//Mode==0：加密
//Mode==其他：解密
    BYTE c,c1,i,*p,Mode;
    int CurrentPos;
    uint32 OriValueL,KeyValueL,OriValueH,KeyValueH;
    BYTE SubStr[17],BinArray[65];

    StringToBinary(KeyStr,BinArray,KeyLen);
    BinaryToHex(BinArray,&KeyValueL,&KeyValueH);
    CurrentPos=0;
    Mode=1;
    if(Mode==0)
    {
        while(1)
        {
            for(i=0;i<8;i++)
            {
                SubStr[i]=0;
            }
            for(i=0;i<8;i++)
            {
                if(i+CurrentPos==SLen)
                    break;
                SubStr[i]=Source[CurrentPos+i];
            }
            StringToBinary(SubStr,BinArray,8);
            BinaryToHex(BinArray,&OriValueL,&OriValueH);
            EDInt(&OriValueL,&OriValueH,KeyValueL,KeyValueH,0);
            for(i=0;i<4;i++)
            //以下这段循环替换下面这句
            //wsprintf(ResultStr+CurrentPos*2,"%02X%02X%02X%02X%02X%02X%02X%02X",(BYTE)EValue,(BYTE)(EValue>>8&0xff),(BYTE)(EValue>>16),(BYTE)(EValue>>24),(BYTE)(EValue>>32),(BYTE)(EValue>>40),(BYTE)(EValue>>48),(BYTE)(EValue>>56));
            {
                c=(OriValueL>>(i*8))&0xff;
                c1=c&0x0f;
                c=(c>>4)&0x0f;
                c=c<10?c+0x30:c+0x37;
                c1=c1<10?c1+0x30:c1+0x37;
                ResultStr[(CurrentPos+i)*2]=c;
                ResultStr[(CurrentPos+i)*2+1]=c1;

                c=(OriValueH>>(i*8))&0xff;
                c1=c&0x0f;
                c=(c>>4)&0x0f;
                c=c<10?c+0x30:c+0x37;
                c1=c1<10?c1+0x30:c1+0x37;
                ResultStr[(CurrentPos+i)*2+8]=c;
                ResultStr[(CurrentPos+i)*2+1+8]=c1;
            }

            CurrentPos+=8;
            if(CurrentPos>=SLen)
            {
                ResultStr[CurrentPos*2]=0;
                break;
            }
        }
    }
    else
    {
        while(1)
        {
            for(i=0;i<16;i++)
              SubStr[i]=Source[CurrentPos*2+i];
            SubStr[i]=0;
            for(i=0;i<8;i++)
            {
                c=SubStr[i*2];
                c1=SubStr[i*2+1];
                c=c>0x39?c-0x37:c-0x30;
                c1=c1>0x39?c1-0x37:c1-0x30;
                c=c<<4;
                c+=c1;
                SubStr[i]=c;
            }
            SubStr[8]=0;
            StringToBinary(SubStr,BinArray,8);
            BinaryToHex(BinArray,&OriValueL,&OriValueH);
            EDInt(&OriValueL,&OriValueH,KeyValueL,KeyValueH,1);
            p=(BYTE *)(&OriValueL);
            for(i=0;i<4;i++)
                ResultStr[i+CurrentPos]=p[i];
            p=(BYTE *)(&OriValueH);
            for(i=0;i<4;i++)
                ResultStr[i+CurrentPos+4]=p[i];
            CurrentPos+=8;
            if(CurrentPos>=SLen/2)
            {
                ResultStr[CurrentPos]=0;
                break;
            }
        }
    }
}
//==========================================
void LongDiv(uint32 *NumberL,uint32 *NumberH,uint32 *ResultL,uint32 *ResultH,uint32 DivisorL,uint32 DivisorH)
{
    BYTE i;
    uint32 Tmp1,Tmp2,Tmp3,Tmp4;

    Tmp1=0;
    Tmp2=0;
    *ResultL=0;
    *ResultH=0;

    for(i=0;i<64;i++)
    {
        Tmp2<<=1;
        if((Tmp1&0x80000000)==0x80000000)
            Tmp2|=1;
        Tmp1<<=1;
        if(((*NumberH)&0x80000000)==0x80000000)
            Tmp1|=1;
        *NumberH<<=1;
        if(((*NumberL)&0x80000000)==0x80000000)
            *NumberH|=1;
        *NumberL<<=1;

        *ResultH<<=1;
        if(((*ResultL)&0x80000000)==0x80000000)
            *ResultH|=1;
        *ResultL<<=1;

        Tmp3=Tmp1;
        Tmp4=Tmp2;
        Tmp3-=DivisorL;
        Tmp4-=DivisorH;
        if((Tmp3&0x80000000)==0x80000000)
        {
            Tmp4--;
        }

        if((Tmp4&0x80000000)==0)
        {
            Tmp2=Tmp4;
            Tmp1=Tmp3;
            (*ResultL)++;
            if((*ResultL)==0)
                (*ResultH)++;
        }
    }
    *NumberL=Tmp1;
    *NumberH=Tmp2;
}
void ConvertJinZhi(uint32 *Number1L,uint32 *Number1H,uint32 *Number2L,uint32 *Number2H,BYTE *Buffer)
{
    BYTE i,j,k;
    uint32 Tmp1,Tmp2,ResultL,ResultH;

    *Number1L=0;
    *Number2L=0;
    *Number1H=0;
    *Number2H=0;

    for(i=10;i;i--)
    {
        if(Buffer[i-1]==0x20)
            continue;
        k=90;
        ResultL=0;
        ResultH=0;
        for(j=0;k;j++)
        {
            Tmp1=*Number1L;
            Tmp2=*Number1H;
            *Number1H<<=1;
            if(((*Number1L)&0x80000000)==0x80000000)
                *Number1H|=1;
            *Number1L<<=1;
            if((k&0x01)==1)
            {
                ResultL+=Tmp1;
                ResultH+=Tmp2;
                if(ResultL<Tmp1)
			    {
				    ResultH++;
			    }
            }
            k>>=1;
        }

        /*Tmp1=*Number1L;
        Tmp2=*Number1H;

        for(j=0;j<89;j++)//替换Number1*90;
        {
            *Number1L+=Tmp1;
            *Number1H+=Tmp2;
            if((*Number1L)<Tmp1)
			{
				(*Number1H)++;
			}
        }*/
        //Number1+=Buffer[i-1]-0x21;
        *Number1L=ResultL;
        *Number1H=ResultH;
        Tmp1=*Number1L;
        Tmp2=(BYTE)(Buffer[i-1]-0x25);
        *Number1L+=Tmp2;
        if(*Number1L<Tmp1)
		{
		    (*Number1H)++;
		}
    }
    for(i=20;i>10;i--)
    {
        if(Buffer[i-1]==0x20)
            continue;

        /*Tmp1=*Number2L;//Number2*=90;
        Tmp2=*Number2H;

        for(j=0;j<89;j++)//替换Number1*90;
        {
            *Number2L+=Tmp1;
            *Number2H+=Tmp2;
            if((*Number2L)<Tmp1)
			{
				(*Number2H)++;
			}
        }*/

        k=90;
        ResultL=0;
        ResultH=0;
        for(j=0;k;j++)
        {
            Tmp1=*Number2L;
            Tmp2=*Number2H;
            *Number2H<<=1;
            if(((*Number2L)&0x80000000)==0x80000000)
                *Number2H|=1;
            *Number2L<<=1;
            if((k&0x01)==1)
            {
                ResultL+=Tmp1;
                ResultH+=Tmp2;
                if(ResultL<Tmp1)
			    {
				    ResultH++;
			    }
            }
            k>>=1;
        }
        //Number2+=Buffer[i-1]-0x21;
        *Number2L=ResultL;
        *Number2H=ResultH;
        Tmp1=*Number2L;
        *Number2L+=(BYTE)(Buffer[i-1]-0x25);
        if(*Number2L<Tmp1)
		{
		    (*Number2H)++;
		}
    }
}
//jQqgP/-Qt(<aq1UY'8d*
//053101001000002010109031317002911101
//mHKb`#l7r=<aq1UY'8d*
//053101001000009010109031317002911101
//+^JRwAk7#)<aq1UY'8d*
//053101001000006010109031317002911101
//==========================================
jint Java_com_hardware_Hardware_DecodeBarcode( JNIEnv* env,
        jobject thiz,jbyteArray data)
{

    BYTE i;
    uint32 Num1L,Num1H,Num2L,Num2H,ResultL,ResultH;
    BYTE Buffer[100];
    unsigned long PassL,PassH;

    jbyte * BufferTemp = (*env)->GetByteArrayElements(env,data,NULL);
	//jint length = (*env)->GetArrayLength(env,data);
	for(i = 0;i<21;i++)
	{
		Buffer[i] = BufferTemp[i];
	}

    PassL = 0x4D2;
    PassH = 0x00;

    if(LoopCountCxj)LoopCountCxj++;
    ConvertJinZhi(&Num1L,&Num1H,&Num2L,&Num2H,Buffer+1);
    EDInt(&Num1L,&Num1H,PassL,PassH,1);
    EDInt(&Num2L,&Num2H,PassL,PassH,1);
    for(i=0;i<38;i++)
        Buffer[i]='0';
    Buffer[38]=0;
    i=18;
    while(1)
    {
        LongDiv(&Num1L,&Num1H,&ResultL,&ResultH,10,0);
        Buffer[i]=Num1L+0x30;
        i--;
        if((ResultL|ResultH)==0)
            break;
        Num1L=ResultL;
        Num1H=ResultH;
    }
    i=37;
    while(1)
    {
        LongDiv(&Num2L,&Num2H,&ResultL,&ResultH,10,0);
        Buffer[i]=Num2L+0x30;
        if((ResultL|ResultH)==0)
            break;
       Num2L=ResultL;
       Num2H=ResultH;
       i--;
    }

    for(i = 0;i<=38;i++)
	{
		BufferTemp[i] = Buffer[i];
	}
    (*env)->ReleaseByteArrayElements(env,data, BufferTemp, 0);
}

//i2c_open(unsigned char* dev, unsigned int timeout, unsigned int retry)
jint Java_com_hardware_Hardware_openI2CDevice( JNIEnv* env, jobject thiz)

{
	int fd;
	int timeout = 3;
	int retry = 3;

	if ((fd = open("/dev/i2c-0", O_RDWR)) < 0)
		return -2;

	if (fd == 0 )
		return -1;

	ioctl(fd, I2C_TIMEOUT, timeout ? timeout : I2C_DEFAULT_TIMEOUT);

	ioctl(fd, I2C_RETRIES, retry ? retry : I2C_DEFAULT_RETRY);

	return fd;
}

//int i2c_read_data(u16 addr, u8 offset, u8 *val)
jint Java_com_hardware_Hardware_readByteDataFromI2C( JNIEnv* env,
        jobject thiz ,jint fd, jint offset ,jbyteArray buffer)
{
	int i,ret = 0;
	struct i2c_rdwr_ioctl_data *data;

	jbyte * buf = (*env)->GetByteArrayElements(env,buffer,NULL);
	jint length = (*env)->GetArrayLength(env,buffer);

	if ((data = (struct i2c_rdwr_ioctl_data *)malloc(sizeof(struct i2c_rdwr_ioctl_data))) == NULL)
	return -1;

	data->nmsgs = 2;
	if ((data->msgs = (struct i2c_msg *)malloc(data->nmsgs * sizeof(struct i2c_msg))) == NULL)
	{
		ret = -2;
		goto errexit3;
	}
	if ((data->msgs[0].buf = (unsigned char *)malloc(sizeof(unsigned char))) == NULL)
	{
		ret = -3;
		goto errexit2;
	}
	if ((data->msgs[1].buf = (unsigned char *)malloc(length * sizeof(unsigned char))) == NULL)
	{
		ret = -4;
		goto errexit1;
	}

	data->msgs[0].addr = 0x50;//芯片地址
	data->msgs[0].flags = 0;//读写标志位
	data->msgs[0].len = 1;//数据长度
	data->msgs[0].buf[0] = offset;//数据地址

	data->msgs[1].addr = 0x50;
	data->msgs[1].flags = I2C_M_RD;
	data->msgs[1].len = length;
	data->msgs[1].buf[0] = 0;

	//if ((ret = __i2c_send(fd, data)) < 0)

	if (data == NULL)
		goto errexit0;

	if (data->msgs == NULL || data->nmsgs == 0)
		goto errexit0;

	if(ioctl(fd, I2C_RDWR, (unsigned long)data) < 0)
		goto errexit0;

	for(i = 0 ;i < length; i++)
		buf[i] = data->msgs[1].buf[i];
		//return data->msgs[1].buf[0];

errexit0:
	free(data->msgs[1].buf);
errexit1:
	free(data->msgs[0].buf);
errexit2:
	free(data->msgs);
errexit3:
	free(data);

	(*env)->ReleaseByteArrayElements(env,buffer, buf, 0);

	return ret;
}

jint Java_com_hardware_Hardware_writeByteDataToI2C( JNIEnv* env,
        jobject thiz ,jint fd, jint offset ,jbyte val)
{
	int ret = 0;
	struct  i2c_rdwr_ioctl_data *data;

	if ((data = (struct i2c_rdwr_ioctl_data *)malloc(sizeof(struct i2c_rdwr_ioctl_data))) == NULL)
		return -1;

	data->nmsgs = 1;
	if ((data->msgs = (struct i2c_msg *)malloc(data->nmsgs * sizeof(struct i2c_msg))) == NULL)
	{
		ret = -2;
		goto errexit2;
	}
	if ((data->msgs[0].buf = (unsigned char *)malloc(2 * sizeof(unsigned char))) == NULL)
	{
		ret = -3;
		goto errexit1;
	}

	data->msgs[0].addr = 0x50;
	data->msgs[0].flags = 0;
	data->msgs[0].len = 2;
	data->msgs[0].buf[0] = offset;
	data->msgs[0].buf[1] = val;

	if (data == NULL)
		goto errexit0;

	if (data->msgs == NULL || data->nmsgs == 0)
		goto errexit0;

	if(ioctl(fd, I2C_RDWR, (unsigned long)data) < 0)
		goto errexit0;

errexit0:
	free(data->msgs[0].buf);
errexit1:
	free(data->msgs);
errexit2:
	free(data);

	return ret;
}

