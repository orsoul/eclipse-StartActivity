package com.fanfull.spi;

public class SPIInstore {
	//先点击确定， 开始扫描基金 袋锁,待都完 成后锁定该批  
	public static byte[] instore_1 = {
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x1E,(byte)0x00,(byte)0x00,
		(byte)0x62,(byte)0x00,(byte)0x01,(byte)0x84,(byte)0x00,(byte)0x06,(byte)0x1F,(byte)0xFC,(byte)0x78,(byte)0x00,(byte)0x02,(byte)0xE0,(byte)0x00,(byte)0x02,(byte)0xE0,(byte)0x03,
		(byte)0xFC,(byte)0xE0,(byte)0x00,(byte)0x20,(byte)0xE0,(byte)0x00,(byte)0x20,(byte)0xE0,(byte)0x03,(byte)0x80,(byte)0xE0,(byte)0x00,(byte)0x40,(byte)0xE0,(byte)0x00,(byte)0x40,
		(byte)0x7C,(byte)0x07,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0xC0,(byte)0x80,(byte)0x00,(byte)0x3F,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"图像",0*/
		
        //请点击扫描认证卡
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x10,(byte)0x03,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x08,(byte)0x0C,(byte)0xFF,(byte)0xFC,(byte)0x0C,
		(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x10,(byte)0x00,(byte)0x3F,(byte)0xF8,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x7C,(byte)0x03,(byte)0x0C,(byte)0x0C,(byte)0xFF,
		(byte)0xFE,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x3F,(byte)0xF0,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x3F,(byte)0xF0,
		(byte)0x0C,(byte)0xB0,(byte)0x10,(byte)0x0D,(byte)0x30,(byte)0x10,(byte)0x0E,(byte)0x3F,(byte)0xF0,(byte)0x0E,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x00,
		(byte)0x31,(byte)0xF0,(byte)0x00,(byte)0x30,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,/*"请",2*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x10,(byte)0x00,
		(byte)0x1F,(byte)0xF8,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x02,(byte)0x10,(byte)0x60,(byte)0x03,(byte)0xEF,
		(byte)0xE0,(byte)0x02,(byte)0x00,(byte)0x60,(byte)0x02,(byte)0x00,(byte)0x60,(byte)0x02,(byte)0x00,(byte)0x60,(byte)0x02,(byte)0x00,(byte)0x60,(byte)0x03,(byte)0xFF,(byte)0xE0,
		(byte)0x02,(byte)0x00,(byte)0x60,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x84,(byte)0x20,(byte)0x08,(byte)0x42,(byte)0x18,(byte)0x08,(byte)0x63,(byte)0x08,(byte)0x18,
		(byte)0x21,(byte)0x0C,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"点",3*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,
		(byte)0x18,(byte)0x10,(byte)0x1F,(byte)0xFF,(byte)0xF8,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,
		(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x0C,(byte)0x3F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x0C,(byte)0x18,(byte)0x30,(byte)0x0C,(byte)0x18,(byte)0x20,
		(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x03,
		(byte)0xE7,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x00,/*"击",4*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,
		(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,
		(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",5*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,
		(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,
		(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",6*/
		
	};
	public static byte[] instore_2 = {
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x38,(byte)0x1F,(byte)0xFF,(byte)0xC0,(byte)0x00,
		(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,
		(byte)0x0C,(byte)0x7F,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x01,(byte)0x81,(byte)0x00,
		(byte)0x01,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x06,(byte)0x01,(byte)0x00,(byte)0x0C,(byte)0x01,(byte)0x00,(byte)0x10,
		(byte)0x01,(byte)0x00,(byte)0x20,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,/*"开",7*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x01,(byte)0x80,(byte)0x06,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x04,
		(byte)0x02,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x20,(byte)0x7F,(byte)0xE4,(byte)0x18,(byte)0x08,(byte)0x48,(byte)0x0C,(byte)0x08,(byte)0xD0,(byte)0x7C,(byte)0x08,(byte)0xDF,
		(byte)0x84,(byte)0x18,(byte)0x80,(byte)0x04,(byte)0x10,(byte)0x80,(byte)0x00,(byte)0x10,(byte)0x88,(byte)0x08,(byte)0x31,(byte)0x8F,(byte)0xF8,(byte)0x31,(byte)0x0C,(byte)0x08,
		(byte)0x0F,(byte)0x0C,(byte)0x08,(byte)0x03,(byte)0x8C,(byte)0x08,(byte)0x06,(byte)0xCC,(byte)0x08,(byte)0x04,(byte)0x4C,(byte)0x08,(byte)0x08,(byte)0x4F,(byte)0xF8,(byte)0x10,
		(byte)0x0C,(byte)0x08,(byte)0x20,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"始",8*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,
		(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,
		(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",9*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,
		(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,
		(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",10*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x81,(byte)0x80,(byte)0x01,(byte)0x01,(byte)0x80,(byte)0x3F,(byte)0xFF,(byte)0xF8,(byte)0x01,
		(byte)0x01,(byte)0x80,(byte)0x01,(byte)0x01,(byte)0x80,(byte)0x01,(byte)0xFF,(byte)0x80,(byte)0x01,(byte)0x01,(byte)0x80,(byte)0x01,(byte)0x01,(byte)0x80,(byte)0x01,(byte)0xFF,
		(byte)0x80,(byte)0x01,(byte)0x01,(byte)0x88,(byte)0x01,(byte)0x01,(byte)0x9C,(byte)0x3E,(byte)0xFE,(byte)0x60,(byte)0x01,(byte)0x19,(byte)0x00,(byte)0x03,(byte)0x10,(byte)0xC0,
		(byte)0x04,(byte)0x10,(byte)0x70,(byte)0x08,(byte)0x11,(byte)0x9C,(byte)0x31,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x10,(byte)0x00,
		(byte)0x10,(byte)0x38,(byte)0x1F,(byte)0xEF,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,/*"基",11*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x38,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x68,(byte)0x00,(byte)0x00,
		(byte)0x44,(byte)0x00,(byte)0x00,(byte)0xC2,(byte)0x00,(byte)0x01,(byte)0x81,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0xC0,(byte)0x06,(byte)0x00,(byte)0x70,(byte)0x08,(byte)0x00,
		(byte)0xBE,(byte)0x11,(byte)0xFF,(byte)0x48,(byte)0x60,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x30,(byte)0x0F,(byte)0xFF,(byte)0xC0,
		(byte)0x04,(byte)0x10,(byte)0x80,(byte)0x02,(byte)0x10,(byte)0xC0,(byte)0x01,(byte)0x10,(byte)0x80,(byte)0x01,(byte)0x91,(byte)0x00,(byte)0x00,(byte)0x91,(byte)0x00,(byte)0x00,
		(byte)0x12,(byte)0x0C,(byte)0x3F,(byte)0xED,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,/*"金",12*/
		
	};
	public static byte[] instore_3 = {
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0D,(byte)0x80,(byte)0x03,(byte)0x08,(byte)0xC0,(byte)0x06,(byte)0x0C,(byte)0x40,(byte)0x06,
		(byte)0x04,(byte)0x3C,(byte)0x0B,(byte)0xFF,(byte)0xC0,(byte)0x12,(byte)0x06,(byte)0x00,(byte)0x22,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x88,(byte)0x02,(byte)0x10,
		(byte)0xE8,(byte)0x06,(byte)0x18,(byte)0x3C,(byte)0x06,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xC8,(byte)0x60,
		(byte)0x01,(byte)0x88,(byte)0xC0,(byte)0x07,(byte)0x85,(byte)0x00,(byte)0x0D,(byte)0x82,(byte)0x00,(byte)0x31,(byte)0x85,(byte)0x80,(byte)0x41,(byte)0xB8,(byte)0xF0,(byte)0x01,
		(byte)0xC0,(byte)0x3E,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"袋",13*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x80,(byte)0x0C,(byte)0x11,(byte)0x98,(byte)0x0C,(byte)0x19,(byte)0x98,(byte)0x08,
		(byte)0xCD,(byte)0x90,(byte)0x0F,(byte)0x0D,(byte)0xA0,(byte)0x10,(byte)0x01,(byte)0xC0,(byte)0x10,(byte)0x11,(byte)0x98,(byte)0x3F,(byte)0x9E,(byte)0x78,(byte)0x24,(byte)0x10,
		(byte)0x18,(byte)0x44,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0xD1,(byte)0x98,(byte)0x3F,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x18,
		(byte)0x04,(byte)0x11,(byte)0x18,(byte)0x04,(byte)0x51,(byte)0x10,(byte)0x04,(byte)0x83,(byte)0x00,(byte)0x07,(byte)0x02,(byte)0xF0,(byte)0x0E,(byte)0x04,(byte)0x1C,(byte)0x04,
		(byte)0x18,(byte)0x06,(byte)0x00,(byte)0xE0,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,/*"锁",14*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x3C,(byte)0x00,(byte)0x00,(byte)0x3C,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,
		(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"，",15*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x03,(byte)0x00,(byte)0x06,(byte)0x02,(byte)0x00,(byte)0x04,(byte)0x02,(byte)0x00,(byte)0x0C,
		(byte)0x7F,(byte)0xF0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x13,(byte)0x82,(byte)0x00,(byte)0x23,(byte)0x02,(byte)0x00,(byte)0x06,(byte)0x02,(byte)0x0C,(byte)0x05,(byte)0xFD,
		(byte)0xF0,(byte)0x0C,(byte)0x00,(byte)0x60,(byte)0x0C,(byte)0x00,(byte)0x40,(byte)0x14,(byte)0xFF,(byte)0xFC,(byte)0x24,(byte)0x00,(byte)0x40,(byte)0x44,(byte)0x10,(byte)0x40,
		(byte)0x04,(byte)0x08,(byte)0x40,(byte)0x04,(byte)0x0C,(byte)0x40,(byte)0x04,(byte)0x0C,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x40,(byte)0x04,
		(byte)0x03,(byte)0xC0,(byte)0x04,(byte)0x00,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,/*"待",16*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x80,(byte)0x06,(byte)0x01,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x0C,
		(byte)0x01,(byte)0x00,(byte)0x08,(byte)0x3F,(byte)0xFC,(byte)0x08,(byte)0xC1,(byte)0x00,(byte)0x10,(byte)0x81,(byte)0x00,(byte)0x21,(byte)0x01,(byte)0x00,(byte)0x3F,(byte)0x01,
		(byte)0x08,(byte)0x22,(byte)0x1F,(byte)0xF8,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0xD8,(byte)0x18,(byte)0x3F,(byte)0x1F,(byte)0xF8,
		(byte)0x10,(byte)0x18,(byte)0x18,(byte)0x00,(byte)0x18,(byte)0x18,(byte)0x00,(byte)0x58,(byte)0x18,(byte)0x07,(byte)0x98,(byte)0x18,(byte)0x38,(byte)0x18,(byte)0x18,(byte)0x20,
		(byte)0x1F,(byte)0xF8,(byte)0x00,(byte)0x18,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,/*"结",17*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x0C,(byte)0x3F,
		(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0x18,(byte)0x20,(byte)0x07,(byte)0xFF,(byte)0xF0,(byte)0x04,(byte)0x18,(byte)0x20,(byte)0x04,(byte)0x18,
		(byte)0x20,(byte)0x04,(byte)0x18,(byte)0x20,(byte)0x04,(byte)0x18,(byte)0x20,(byte)0x07,(byte)0xFF,(byte)0xE0,(byte)0x04,(byte)0x7C,(byte)0x20,(byte)0x08,(byte)0x5A,(byte)0x00,
		(byte)0x00,(byte)0xDA,(byte)0x00,(byte)0x01,(byte)0x99,(byte)0x00,(byte)0x03,(byte)0x18,(byte)0xC0,(byte)0x06,(byte)0x18,(byte)0x70,(byte)0x08,(byte)0x18,(byte)0x3E,(byte)0x10,
		(byte)0x18,(byte)0x18,(byte)0x60,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"束",18*/

	};
	public static byte[] instore_4 = {
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x03,(byte)0xF8,(byte)0x07,(byte)0xFC,(byte)0x00,(byte)0x04,
		(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x07,(byte)0xFF,(byte)0xF0,(byte)0x04,(byte)0x00,
		(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x04,(byte)0x40,(byte)0x30,(byte)0x04,(byte)0x40,(byte)0x30,
		(byte)0x0C,(byte)0x40,(byte)0x30,(byte)0x08,(byte)0x40,(byte)0x30,(byte)0x08,(byte)0x40,(byte)0x30,(byte)0x10,(byte)0x40,(byte)0x30,(byte)0x10,(byte)0xFF,(byte)0xF0,(byte)0x20,
		(byte)0xC0,(byte)0x30,(byte)0x40,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"后",19*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x3C,(byte)0x00,(byte)0x00,(byte)0x3C,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,
		(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"，",20*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x80,(byte)0x0C,(byte)0x11,(byte)0x98,(byte)0x0C,(byte)0x19,(byte)0x98,(byte)0x08,
		(byte)0xCD,(byte)0x90,(byte)0x0F,(byte)0x0D,(byte)0xA0,(byte)0x10,(byte)0x01,(byte)0xC0,(byte)0x10,(byte)0x11,(byte)0x98,(byte)0x3F,(byte)0x9E,(byte)0x78,(byte)0x24,(byte)0x10,
		(byte)0x18,(byte)0x44,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0xD1,(byte)0x98,(byte)0x3F,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x18,
		(byte)0x04,(byte)0x11,(byte)0x18,(byte)0x04,(byte)0x51,(byte)0x10,(byte)0x04,(byte)0x83,(byte)0x00,(byte)0x07,(byte)0x02,(byte)0xF0,(byte)0x0E,(byte)0x04,(byte)0x1C,(byte)0x04,
		(byte)0x18,(byte)0x06,(byte)0x00,(byte)0xE0,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,/*"锁",21*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0x08,(byte)0x00,(byte)0x08,
		(byte)0x00,(byte)0x08,(byte)0x0F,(byte)0xFF,(byte)0xFC,(byte)0x18,(byte)0x00,(byte)0x10,(byte)0x30,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x1F,(byte)0xFF,
		(byte)0xF8,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x03,(byte)0x18,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x20,(byte)0x02,(byte)0x1F,(byte)0xF0,
		(byte)0x06,(byte)0x18,(byte)0x00,(byte)0x05,(byte)0x18,(byte)0x00,(byte)0x05,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0xD8,(byte)0x00,(byte)0x08,(byte)0x78,(byte)0x00,(byte)0x10,
		(byte)0x1F,(byte)0xFC,(byte)0x20,(byte)0x01,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x00,/*"定",22*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x06,(byte)0x01,(byte)0x00,(byte)0x06,
		(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x20,(byte)0x04,(byte)0x08,(byte)0x30,(byte)0x3C,(byte)0x10,
		(byte)0x60,(byte)0x04,(byte)0x3F,(byte)0xC0,(byte)0x04,(byte)0x20,(byte)0x88,(byte)0x04,(byte)0x01,(byte)0x8C,(byte)0x04,(byte)0x03,(byte)0x18,(byte)0x04,(byte)0x06,(byte)0x30,
		(byte)0x04,(byte)0x48,(byte)0x60,(byte)0x04,(byte)0xB0,(byte)0xC0,(byte)0x05,(byte)0x41,(byte)0xA0,(byte)0x06,(byte)0x03,(byte)0x18,(byte)0x04,(byte)0x04,(byte)0x0C,(byte)0x00,
		(byte)0x18,(byte)0x04,(byte)0x00,(byte)0xE0,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,/*"该",23*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x20,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0xC0,(byte)0x04,(byte)0x30,(byte)0x80,(byte)0x04,
		(byte)0x30,(byte)0x80,(byte)0x05,(byte)0xB0,(byte)0x80,(byte)0x3E,(byte)0x70,(byte)0x84,(byte)0x04,(byte)0x30,(byte)0x8E,(byte)0x04,(byte)0x3E,(byte)0x98,(byte)0x04,(byte)0x71,
		(byte)0xB0,(byte)0x07,(byte)0xB0,(byte)0xC0,(byte)0x0C,(byte)0x30,(byte)0x80,(byte)0x74,(byte)0x30,(byte)0x80,(byte)0x24,(byte)0x30,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0x80,
		(byte)0x04,(byte)0x30,(byte)0x84,(byte)0x04,(byte)0x31,(byte)0x84,(byte)0x04,(byte)0x32,(byte)0x84,(byte)0x04,(byte)0x3C,(byte)0x84,(byte)0x04,(byte)0x30,(byte)0xC6,(byte)0x1C,
		(byte)0x30,(byte)0xFC,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"批",24*/
	};
	public static byte[] instore_5 = {//请将手持靠近袋锁
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x10,(byte)0x03,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x08,(byte)0x0C,(byte)0xFF,(byte)0xFC,(byte)0x0C,
		(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x10,(byte)0x00,(byte)0x3F,(byte)0xF8,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x7C,(byte)0x03,(byte)0x0C,(byte)0x0C,(byte)0xFF,
		(byte)0xFE,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x3F,(byte)0xF0,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x3F,(byte)0xF0,
		(byte)0x0C,(byte)0xB0,(byte)0x10,(byte)0x0D,(byte)0x30,(byte)0x10,(byte)0x0E,(byte)0x3F,(byte)0xF0,(byte)0x0E,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x00,
		(byte)0x31,(byte)0xF0,(byte)0x00,(byte)0x30,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,/*"请",1*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x02,(byte)0x06,(byte)0x00,(byte)0x02,(byte)0x0C,(byte)0x00,(byte)0x02,(byte)0x08,(byte)0x10,(byte)0x02,
		(byte)0x1F,(byte)0xF8,(byte)0x22,(byte)0x10,(byte)0x30,(byte)0x32,(byte)0x28,(byte)0x60,(byte)0x1A,(byte)0x4C,(byte)0x40,(byte)0x1A,(byte)0x85,(byte)0x80,(byte)0x02,(byte)0x03,
		(byte)0x00,(byte)0x02,(byte)0x04,(byte)0x20,(byte)0x02,(byte)0x18,(byte)0x20,(byte)0x06,(byte)0xE0,(byte)0x24,(byte)0x0A,(byte)0xFF,(byte)0xFE,(byte)0x12,(byte)0x20,(byte)0x20,
		(byte)0x22,(byte)0x18,(byte)0x20,(byte)0x62,(byte)0x08,(byte)0x20,(byte)0x02,(byte)0x0C,(byte)0x20,(byte)0x02,(byte)0x08,(byte)0x20,(byte)0x02,(byte)0x00,(byte)0x20,(byte)0x02,
		(byte)0x03,(byte)0xE0,(byte)0x02,(byte)0x00,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x00,/*"将",2*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x60,(byte)0x00,(byte)0x0F,(byte)0xF0,(byte)0x1F,(byte)0xF0,(byte)0x00,(byte)0x00,
		(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x20,(byte)0x00,(byte)0x10,(byte)0x70,(byte)0x0F,(byte)0xFF,(byte)0x80,(byte)0x00,(byte)0x10,
		(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x0C,(byte)0x3F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x10,(byte)0x00,
		(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,
		(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"手",3*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x03,(byte)0x80,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x04,
		(byte)0x7F,(byte)0xF8,(byte)0x04,(byte)0x81,(byte)0x00,(byte)0x3F,(byte)0xC1,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x0C,(byte)0x04,(byte)0x7E,
		(byte)0xF0,(byte)0x04,(byte)0x80,(byte)0x60,(byte)0x07,(byte)0x00,(byte)0x40,(byte)0x1C,(byte)0x00,(byte)0x4C,(byte)0x34,(byte)0x7F,(byte)0xF0,(byte)0x24,(byte)0x40,(byte)0x40,
		(byte)0x04,(byte)0x30,(byte)0x40,(byte)0x04,(byte)0x18,(byte)0x40,(byte)0x04,(byte)0x08,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x40,(byte)0x04,(byte)0x00,(byte)0x40,(byte)0x1C,
		(byte)0x03,(byte)0xC0,(byte)0x0C,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,/*"持",4*/
		
		
	};
	
	public static byte[] instore_6 = {
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x18,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x40,(byte)0x07,(byte)0xFF,(byte)0xE0,(byte)0x04,
		(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x0C,(byte)0x3F,(byte)0xE7,(byte)0xF0,(byte)0x02,(byte)0x00,(byte)0x40,(byte)0x03,(byte)0xFF,(byte)0xC0,(byte)0x02,(byte)0x00,
		(byte)0x40,(byte)0x03,(byte)0xFF,(byte)0xC0,(byte)0x02,(byte)0x84,(byte)0x00,(byte)0x00,(byte)0x46,(byte)0x08,(byte)0x3F,(byte)0xC7,(byte)0xFC,(byte)0x00,(byte)0x46,(byte)0x00,
		(byte)0x00,(byte)0x46,(byte)0x10,(byte)0x0F,(byte)0xC7,(byte)0xE0,(byte)0x00,(byte)0x46,(byte)0x00,(byte)0x7F,(byte)0xC7,(byte)0xFC,(byte)0x00,(byte)0x46,(byte)0x00,(byte)0x00,
		(byte)0x46,(byte)0x00,(byte)0x00,(byte)0x46,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"靠",5*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x30,(byte)0x08,(byte)0x01,(byte)0xF0,(byte)0x04,(byte)0x3E,(byte)0x00,(byte)0x04,
		(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x04,(byte)0x20,(byte)0x08,(byte)0x7E,(byte)0x3F,(byte)0xFC,(byte)0x04,(byte)0x20,
		(byte)0xC0,(byte)0x04,(byte)0x20,(byte)0xC0,(byte)0x04,(byte)0x20,(byte)0xC0,(byte)0x04,(byte)0x20,(byte)0xC0,(byte)0x04,(byte)0x20,(byte)0xC0,(byte)0x04,(byte)0x40,(byte)0xC0,
		(byte)0x04,(byte)0x40,(byte)0xC0,(byte)0x04,(byte)0x80,(byte)0xC0,(byte)0x0D,(byte)0x00,(byte)0xC0,(byte)0x13,(byte)0x00,(byte)0x80,(byte)0x60,(byte)0xC0,(byte)0x00,(byte)0x20,
		(byte)0x3F,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"近",6*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0D,(byte)0x80,(byte)0x03,(byte)0x08,(byte)0xC0,(byte)0x06,(byte)0x0C,(byte)0x40,(byte)0x06,
		(byte)0x04,(byte)0x3C,(byte)0x0B,(byte)0xFF,(byte)0xC0,(byte)0x12,(byte)0x06,(byte)0x00,(byte)0x22,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x88,(byte)0x02,(byte)0x10,
		(byte)0xE8,(byte)0x06,(byte)0x18,(byte)0x3C,(byte)0x06,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xC8,(byte)0x60,
		(byte)0x01,(byte)0x88,(byte)0xC0,(byte)0x07,(byte)0x85,(byte)0x00,(byte)0x0D,(byte)0x82,(byte)0x00,(byte)0x31,(byte)0x85,(byte)0x80,(byte)0x41,(byte)0xB8,(byte)0xF0,(byte)0x01,
		(byte)0xC0,(byte)0x3E,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"袋",7*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x80,(byte)0x0C,(byte)0x11,(byte)0x98,(byte)0x0C,(byte)0x19,(byte)0x98,(byte)0x08,
		(byte)0xCD,(byte)0x90,(byte)0x0F,(byte)0x0D,(byte)0xA0,(byte)0x10,(byte)0x01,(byte)0xC0,(byte)0x10,(byte)0x11,(byte)0x98,(byte)0x3F,(byte)0x9E,(byte)0x78,(byte)0x24,(byte)0x10,
		(byte)0x18,(byte)0x44,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0xD1,(byte)0x98,(byte)0x3F,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x18,
		(byte)0x04,(byte)0x11,(byte)0x18,(byte)0x04,(byte)0x51,(byte)0x10,(byte)0x04,(byte)0x83,(byte)0x00,(byte)0x07,(byte)0x02,(byte)0xF0,(byte)0x0E,(byte)0x04,(byte)0x1C,(byte)0x04,
		(byte)0x18,(byte)0x06,(byte)0x00,(byte)0xE0,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,/*"锁",8*/
	};
	
	public static byte[] instore_7 = {
		//请上传任务
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x10,(byte)0x03,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x08,(byte)0x0C,(byte)0xFF,(byte)0xFC,(byte)0x0C,
		(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x10,(byte)0x00,(byte)0x3F,(byte)0xF8,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x7C,(byte)0x03,(byte)0x0C,(byte)0x0C,(byte)0xFF,
		(byte)0xFE,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x3F,(byte)0xF0,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x3F,(byte)0xF0,
		(byte)0x0C,(byte)0xB0,(byte)0x10,(byte)0x0D,(byte)0x30,(byte)0x10,(byte)0x0E,(byte)0x3F,(byte)0xF0,(byte)0x0E,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x00,
		(byte)0x31,(byte)0xF0,(byte)0x00,(byte)0x30,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,
		(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x20,(byte)0x00,(byte)0x30,(byte)0x70,(byte)0x00,(byte)0x3F,
		(byte)0x80,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,
		(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x0C,(byte)0x3F,
		(byte)0xCF,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x83,(byte)0x00,(byte)0x03,(byte)0x02,(byte)0x00,(byte)0x03,(byte)0x02,(byte)0x00,(byte)0x02,
		(byte)0x02,(byte)0x00,(byte)0x06,(byte)0x02,(byte)0x18,(byte)0x04,(byte)0x7F,(byte)0xE0,(byte)0x0E,(byte)0x04,(byte)0x00,(byte)0x0E,(byte)0x04,(byte)0x00,(byte)0x16,(byte)0x04,
		(byte)0x04,(byte)0x17,(byte)0xFF,(byte)0xFE,(byte)0x26,(byte)0x0C,(byte)0x00,(byte)0x46,(byte)0x08,(byte)0x00,(byte)0x06,(byte)0x08,(byte)0x10,(byte)0x06,(byte)0x1F,(byte)0xF8,
		(byte)0x06,(byte)0x00,(byte)0x20,(byte)0x06,(byte)0x00,(byte)0x40,(byte)0x06,(byte)0x10,(byte)0x80,(byte)0x06,(byte)0x0F,(byte)0x00,(byte)0x06,(byte)0x03,(byte)0x80,(byte)0x06,
		(byte)0x00,(byte)0xE0,(byte)0x06,(byte)0x00,(byte)0x60,(byte)0x00,(byte)0x00,(byte)0x00,
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x10,(byte)0x03,(byte)0x01,(byte)0xF8,(byte)0x02,
		(byte)0x7F,(byte)0x00,(byte)0x06,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x0C,(byte)0x03,(byte)0x00,(byte)0x0C,(byte)0x03,(byte)0x00,(byte)0x14,(byte)0x03,
		(byte)0x00,(byte)0x14,(byte)0x03,(byte)0x00,(byte)0x24,(byte)0xFF,(byte)0xFC,(byte)0x44,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,
		(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x04,(byte)0x04,
		(byte)0xFF,(byte)0xFE,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x80,(byte)0x80,(byte)0x01,(byte)0x7F,(byte)0xC0,(byte)0x03,
		(byte)0x81,(byte)0x80,(byte)0x02,(byte)0x43,(byte)0x00,(byte)0x04,(byte)0x62,(byte)0x00,(byte)0x08,(byte)0x34,(byte)0x00,(byte)0x10,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x3C,
		(byte)0x00,(byte)0x00,(byte)0x43,(byte)0x00,(byte)0x01,(byte)0xB1,(byte)0xF0,(byte)0x06,(byte)0x30,(byte)0x3C,(byte)0x38,(byte)0x20,(byte)0x40,(byte)0x07,(byte)0xFF,(byte)0xC0,
		(byte)0x00,(byte)0x20,(byte)0x40,(byte)0x00,(byte)0x60,(byte)0xC0,(byte)0x00,(byte)0x40,(byte)0xC0,(byte)0x00,(byte)0x80,(byte)0xC0,(byte)0x01,(byte)0x00,(byte)0x80,(byte)0x02,
		(byte)0x0F,(byte)0x80,(byte)0x1C,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
	};
	
	public static byte[] instore_8 = {
		//请锁定该批
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x10,(byte)0x03,(byte)0x00,(byte)0x08,(byte)0x03,(byte)0x08,(byte)0x0C,(byte)0xFF,(byte)0xFC,(byte)0x0C,
		(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x10,(byte)0x00,(byte)0x3F,(byte)0xF8,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x7C,(byte)0x03,(byte)0x0C,(byte)0x0C,(byte)0xFF,
		(byte)0xFE,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x3F,(byte)0xF0,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x3F,(byte)0xF0,
		(byte)0x0C,(byte)0xB0,(byte)0x10,(byte)0x0D,(byte)0x30,(byte)0x10,(byte)0x0E,(byte)0x3F,(byte)0xF0,(byte)0x0E,(byte)0x30,(byte)0x10,(byte)0x0C,(byte)0x30,(byte)0x10,(byte)0x00,
		(byte)0x31,(byte)0xF0,(byte)0x00,(byte)0x30,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x00,
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x80,(byte)0x0C,(byte)0x11,(byte)0x98,(byte)0x0C,(byte)0x19,(byte)0x98,(byte)0x08,
		(byte)0xCD,(byte)0x90,(byte)0x0F,(byte)0x0D,(byte)0xA0,(byte)0x10,(byte)0x01,(byte)0xC0,(byte)0x10,(byte)0x11,(byte)0x98,(byte)0x3F,(byte)0x9E,(byte)0x78,(byte)0x24,(byte)0x10,
		(byte)0x18,(byte)0x44,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0xD1,(byte)0x98,(byte)0x3F,(byte)0x11,(byte)0x98,(byte)0x04,(byte)0x11,(byte)0x18,
		(byte)0x04,(byte)0x11,(byte)0x18,(byte)0x04,(byte)0x51,(byte)0x10,(byte)0x04,(byte)0x83,(byte)0x00,(byte)0x07,(byte)0x02,(byte)0xF0,(byte)0x0E,(byte)0x04,(byte)0x1C,(byte)0x04,
		(byte)0x18,(byte)0x06,(byte)0x00,(byte)0xE0,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,/*"锁",8*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0x08,(byte)0x00,(byte)0x08,
		(byte)0x00,(byte)0x08,(byte)0x0F,(byte)0xFF,(byte)0xFC,(byte)0x18,(byte)0x00,(byte)0x10,(byte)0x30,(byte)0x00,(byte)0x20,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x1F,(byte)0xFF,
		(byte)0xF8,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x03,(byte)0x18,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x20,(byte)0x02,(byte)0x1F,(byte)0xF0,
		(byte)0x06,(byte)0x18,(byte)0x00,(byte)0x05,(byte)0x18,(byte)0x00,(byte)0x05,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0xD8,(byte)0x00,(byte)0x08,(byte)0x78,(byte)0x00,(byte)0x10,
		(byte)0x1F,(byte)0xFC,(byte)0x20,(byte)0x01,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x00,/*"定",22*/
		
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x06,(byte)0x01,(byte)0x00,(byte)0x06,
		(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x20,(byte)0x04,(byte)0x08,(byte)0x30,(byte)0x3C,(byte)0x10,
		(byte)0x60,(byte)0x04,(byte)0x3F,(byte)0xC0,(byte)0x04,(byte)0x20,(byte)0x88,(byte)0x04,(byte)0x01,(byte)0x8C,(byte)0x04,(byte)0x03,(byte)0x18,(byte)0x04,(byte)0x06,(byte)0x30,
		(byte)0x04,(byte)0x48,(byte)0x60,(byte)0x04,(byte)0xB0,(byte)0xC0,(byte)0x05,(byte)0x41,(byte)0xA0,(byte)0x06,(byte)0x03,(byte)0x18,(byte)0x04,(byte)0x04,(byte)0x0C,(byte)0x00,
		(byte)0x18,(byte)0x04,(byte)0x00,(byte)0xE0,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,/*"该",23*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x20,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0xC0,(byte)0x04,(byte)0x30,(byte)0x80,(byte)0x04,
		(byte)0x30,(byte)0x80,(byte)0x05,(byte)0xB0,(byte)0x80,(byte)0x3E,(byte)0x70,(byte)0x84,(byte)0x04,(byte)0x30,(byte)0x8E,(byte)0x04,(byte)0x3E,(byte)0x98,(byte)0x04,(byte)0x71,
		(byte)0xB0,(byte)0x07,(byte)0xB0,(byte)0xC0,(byte)0x0C,(byte)0x30,(byte)0x80,(byte)0x74,(byte)0x30,(byte)0x80,(byte)0x24,(byte)0x30,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0x80,
		(byte)0x04,(byte)0x30,(byte)0x84,(byte)0x04,(byte)0x31,(byte)0x84,(byte)0x04,(byte)0x32,(byte)0x84,(byte)0x04,(byte)0x3C,(byte)0x84,(byte)0x04,(byte)0x30,(byte)0xC6,(byte)0x1C,
		(byte)0x30,(byte)0xFC,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"批",24*/
	};
}
