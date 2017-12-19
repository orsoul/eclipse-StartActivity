package com.fanfull.spi;

public class SPIText {
	public static byte[] buffer2 = {
			// 权限确认
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x02,(byte)0x10,(byte)0x10,(byte)0x02,(byte)0x48,(byte)0x10,(byte)0x7F,(byte)0xE8,(byte)0x30,(byte)0x06,(byte)0x08,(byte)0x30,(byte)0x06,(byte)0x08,(byte)0x20,(byte)0x06,(byte)0x08,
		(byte)0x20,(byte)0x0F,(byte)0x84,(byte)0x60,(byte)0x0A,(byte)0xC4,(byte)0x40,(byte)0x0A,(byte)0x64,(byte)0xC0,(byte)0x12,(byte)0x02,(byte)0x80,(byte)0x12,(byte)0x03,(byte)0x80,(byte)0x22,(byte)0x01,(byte)0x00,(byte)0x42,(byte)0x03,(byte)0x80,(byte)0x02,(byte)0x04,(byte)0xC0,(byte)0x06,(byte)0x08,(byte)0x60,(byte)0x06,(byte)0x10,(byte)0x38,(byte)0x06,
		(byte)0x60,(byte)0x1E,(byte)0x07,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"权",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x1F,(byte)0x9F,(byte)0xF8,(byte)0x11,(byte)0x10,(byte)0x30,(byte)0x11,(byte)0x10,(byte)0x30,(byte)0x12,(byte)0x10,(byte)0x30,(byte)0x12,(byte)0x1F,(byte)0xF0,(byte)0x12,(byte)0x10,(byte)0x30,(byte)0x14,(byte)0x10,(byte)0x30,(byte)0x12,(byte)0x10,
		(byte)0x30,(byte)0x11,(byte)0x1F,(byte)0xF0,(byte)0x11,(byte)0x14,(byte)0x20,(byte)0x10,(byte)0x92,(byte)0x18,(byte)0x10,(byte)0x92,(byte)0x18,(byte)0x10,(byte)0x91,(byte)0x60,(byte)0x1F,(byte)0x91,(byte)0x80,(byte)0x13,(byte)0x10,(byte)0x80,(byte)0x10,(byte)0x10,(byte)0xC0,(byte)0x10,(byte)0x13,(byte)0x60,(byte)0x10,(byte)0x1C,(byte)0x38,(byte)0x10,
		(byte)0x38,(byte)0x1C,(byte)0x10,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"限",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x20,(byte)0x7F,(byte)0xCF,(byte)0xF0,(byte)0x04,(byte)0x08,(byte)0x60,(byte)0x04,(byte)0x10,(byte)0x40,(byte)0x0C,(byte)0x10,(byte)0x80,(byte)0x08,(byte)0x3F,(byte)0xFC,(byte)0x08,(byte)0x58,(byte)0x88,(byte)0x18,(byte)0x98,
		(byte)0x88,(byte)0x1F,(byte)0x98,(byte)0x88,(byte)0x18,(byte)0x9F,(byte)0xF8,(byte)0x28,(byte)0x98,(byte)0x88,(byte)0x48,(byte)0x98,(byte)0x88,(byte)0x08,(byte)0x98,(byte)0x88,(byte)0x08,(byte)0x9F,(byte)0xF8,(byte)0x08,(byte)0x90,(byte)0x88,(byte)0x0F,(byte)0x90,(byte)0x88,(byte)0x08,(byte)0x30,(byte)0x88,(byte)0x18,(byte)0x20,(byte)0x88,(byte)0x00,
		(byte)0x40,(byte)0xF8,(byte)0x00,(byte)0x80,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"确",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x06,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x04,(byte)0x03,(byte)0x00,(byte)0x7E,(byte)0x03,
		(byte)0x00,(byte)0x04,(byte)0x07,(byte)0x00,(byte)0x04,(byte)0x07,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x80,(byte)0x04,(byte)0x04,(byte)0x80,(byte)0x04,(byte)0x4C,(byte)0x80,(byte)0x04,(byte)0x48,(byte)0x40,(byte)0x05,(byte)0x98,(byte)0x40,(byte)0x07,(byte)0x10,(byte)0x20,(byte)0x06,(byte)0x20,(byte)0x30,(byte)0x04,(byte)0x40,(byte)0x18,(byte)0x00,
		(byte)0x80,(byte)0x0E,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"认",3*/
	};
	public static byte[] buffer3 = {

			// 选择操作类型
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x0C,(byte)0x32,(byte)0x00,(byte)0x04,(byte)0x22,(byte)0x20,(byte)0x04,(byte)0x3F,(byte)0xF0,(byte)0x00,(byte)0x42,(byte)0x00,(byte)0x00,(byte)0x42,(byte)0x00,(byte)0x04,(byte)0x82,(byte)0x00,(byte)0x7E,(byte)0x02,
		(byte)0x18,(byte)0x05,(byte)0xFD,(byte)0xE0,(byte)0x04,(byte)0x09,(byte)0x80,(byte)0x04,(byte)0x09,(byte)0x80,(byte)0x04,(byte)0x19,(byte)0x80,(byte)0x04,(byte)0x11,(byte)0x80,(byte)0x04,(byte)0x31,(byte)0x88,(byte)0x04,(byte)0x21,(byte)0x88,(byte)0x0C,(byte)0x40,(byte)0xFC,(byte)0x33,(byte)0x80,(byte)0x00,(byte)0x61,(byte)0x80,(byte)0x00,(byte)0x00,
		(byte)0x7F,(byte)0xFC,(byte)0x00,(byte)0x07,(byte)0xF8,(byte)0x00,(byte)0x00,(byte)0x00,/*"选",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x10,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x02,(byte)0x08,(byte)0x30,(byte)0x02,(byte)0x08,(byte)0x20,(byte)0x02,(byte)0x44,(byte)0x40,(byte)0x3F,(byte)0x82,(byte)0x80,(byte)0x02,(byte)0x01,(byte)0x80,(byte)0x02,(byte)0x06,(byte)0xC0,(byte)0x02,(byte)0x49,
		(byte)0x3E,(byte)0x03,(byte)0xB1,(byte)0x88,(byte)0x0E,(byte)0xC1,(byte)0x10,(byte)0x3A,(byte)0x01,(byte)0x18,(byte)0x22,(byte)0x1F,(byte)0xE0,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0xFF,(byte)0xFC,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x1E,
		(byte)0x01,(byte)0x00,(byte)0x04,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"择",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xE0,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0D,(byte)0x1F,(byte)0xC0,(byte)0x7F,(byte)0x80,(byte)0x00,(byte)0x0C,(byte)0x80,(byte)0x08,(byte)0x0C,(byte)0xFC,(byte)0xFC,(byte)0x0C,(byte)0xC8,
		(byte)0x88,(byte)0x0F,(byte)0xC8,(byte)0x88,(byte)0x1C,(byte)0xF8,(byte)0xF8,(byte)0x7C,(byte)0xCE,(byte)0x80,(byte)0x0C,(byte)0x03,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFC,(byte)0x0C,(byte)0x0F,(byte)0x00,(byte)0x0C,(byte)0x0A,(byte)0x80,(byte)0x0C,(byte)0x12,(byte)0x40,(byte)0x0C,(byte)0x22,(byte)0x30,(byte)0x0C,(byte)0x42,(byte)0x1E,(byte)0x6D,
		(byte)0x82,(byte)0x08,(byte)0x1A,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"操",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x03,(byte)0x8C,(byte)0x00,(byte)0x03,(byte)0x08,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x00,(byte)0x06,(byte)0x10,(byte)0x08,(byte)0x04,(byte)0x1F,(byte)0xFC,(byte)0x04,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x44,(byte)0x00,(byte)0x14,(byte)0x84,
		(byte)0x00,(byte)0x25,(byte)0x04,(byte)0x18,(byte)0x44,(byte)0x07,(byte)0xE0,(byte)0x44,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x18,(byte)0x04,(byte)0x07,(byte)0xE0,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,
		(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"作",3*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x04,(byte)0x0C,(byte)0x00,(byte)0x03,(byte)0x08,(byte)0x70,(byte)0x01,(byte)0x88,(byte)0xC0,(byte)0x00,(byte)0x88,(byte)0x80,(byte)0x00,(byte)0x09,(byte)0x0C,(byte)0x3F,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x6C,(byte)0x00,(byte)0x00,(byte)0xCB,(byte)0xC0,(byte)0x01,(byte)0x88,
		(byte)0x70,(byte)0x02,(byte)0x18,(byte)0x18,(byte)0x0C,(byte)0x10,(byte)0x08,(byte)0x30,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x04,(byte)0x7F,(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x34,(byte)0x00,(byte)0x00,(byte)0x24,(byte)0x00,(byte)0x00,(byte)0x63,(byte)0x00,(byte)0x00,(byte)0xC1,(byte)0x80,(byte)0x01,(byte)0x80,(byte)0xE0,(byte)0x03,
		(byte)0x00,(byte)0x3E,(byte)0x3C,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x00,/*"类",4*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x08,(byte)0x08,(byte)0x1F,(byte)0xFC,(byte)0x08,(byte)0x02,(byte)0x20,(byte)0xC8,(byte)0x02,(byte)0x20,(byte)0x88,(byte)0x02,(byte)0x20,(byte)0x88,(byte)0x02,(byte)0x2C,(byte)0x88,(byte)0x1F,(byte)0xF0,(byte)0x88,(byte)0x02,(byte)0x20,(byte)0x88,(byte)0x06,(byte)0x20,
		(byte)0x88,(byte)0x04,(byte)0x20,(byte)0x08,(byte)0x04,(byte)0x20,(byte)0x08,(byte)0x08,(byte)0x20,(byte)0xF8,(byte)0x10,(byte)0x10,(byte)0x30,(byte)0x20,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x20,(byte)0x07,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x08,(byte)0x1F,
		(byte)0xFF,(byte)0xFC,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"型",5*/

	};
	public static byte[] bufBatch = {
			// 批量扫描操作


		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x20,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0xC0,(byte)0x04,(byte)0x30,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0x80,(byte)0x05,(byte)0xB0,(byte)0x80,(byte)0x3E,(byte)0x70,(byte)0x84,(byte)0x04,(byte)0x30,(byte)0x8E,(byte)0x04,(byte)0x3E,(byte)0x98,(byte)0x04,(byte)0x71,
		(byte)0xB0,(byte)0x07,(byte)0xB0,(byte)0xC0,(byte)0x0C,(byte)0x30,(byte)0x80,(byte)0x74,(byte)0x30,(byte)0x80,(byte)0x24,(byte)0x30,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0x80,(byte)0x04,(byte)0x30,(byte)0x84,(byte)0x04,(byte)0x31,(byte)0x84,(byte)0x04,(byte)0x32,(byte)0x84,(byte)0x04,(byte)0x3C,(byte)0x84,(byte)0x04,(byte)0x30,(byte)0xC6,(byte)0x1C,
		(byte)0x30,(byte)0xFC,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"批",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xE0,(byte)0x06,(byte)0x00,(byte)0x40,(byte)0x06,(byte)0x00,(byte)0x40,(byte)0x07,(byte)0xFF,(byte)0xC0,(byte)0x06,(byte)0x00,(byte)0x40,(byte)0x07,(byte)0xFF,(byte)0xC0,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x7F,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x07,(byte)0xFF,(byte)0xE0,(byte)0x04,(byte)0x18,(byte)0x60,(byte)0x04,(byte)0x18,(byte)0x60,(byte)0x07,(byte)0xFF,(byte)0xE0,(byte)0x04,(byte)0x18,(byte)0x60,(byte)0x07,(byte)0xFF,(byte)0xE0,(byte)0x04,(byte)0x18,(byte)0x40,(byte)0x00,(byte)0x18,(byte)0x20,(byte)0x0F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,
		(byte)0x18,(byte)0x0C,(byte)0x3F,(byte)0xE7,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x00,/*"量",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xE0,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0D,(byte)0x1F,(byte)0xC0,(byte)0x7F,(byte)0x80,(byte)0x00,(byte)0x0C,(byte)0x80,(byte)0x08,(byte)0x0C,(byte)0xFC,(byte)0xFC,(byte)0x0C,(byte)0xC8,
		(byte)0x88,(byte)0x0F,(byte)0xC8,(byte)0x88,(byte)0x1C,(byte)0xF8,(byte)0xF8,(byte)0x7C,(byte)0xCE,(byte)0x80,(byte)0x0C,(byte)0x03,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFC,(byte)0x0C,(byte)0x0F,(byte)0x00,(byte)0x0C,(byte)0x0A,(byte)0x80,(byte)0x0C,(byte)0x12,(byte)0x40,(byte)0x0C,(byte)0x22,(byte)0x30,(byte)0x0C,(byte)0x42,(byte)0x1E,(byte)0x6D,
		(byte)0x82,(byte)0x08,(byte)0x1A,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"操",4*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x03,(byte)0x8C,(byte)0x00,(byte)0x03,(byte)0x08,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x00,(byte)0x06,(byte)0x10,(byte)0x08,(byte)0x04,(byte)0x1F,(byte)0xFC,(byte)0x04,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x44,(byte)0x00,(byte)0x14,(byte)0x84,
		(byte)0x00,(byte)0x25,(byte)0x04,(byte)0x18,(byte)0x44,(byte)0x07,(byte)0xE0,(byte)0x44,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x18,(byte)0x04,(byte)0x07,(byte)0xE0,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,
		(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"作",5*/


	};
	public static byte[] bufGeneral = {
			// 一般扫描操作
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x1C,(byte)0x3F,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"一",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x20,(byte)0x02,(byte)0x07,(byte)0xF0,(byte)0x0C,(byte)0x64,(byte)0x20,(byte)0x0B,(byte)0xC4,(byte)0x20,(byte)0x08,(byte)0x44,(byte)0x20,(byte)0x0A,(byte)0x44,(byte)0x20,(byte)0x0B,(byte)0x44,(byte)0x3E,(byte)0x09,(byte)0x48,(byte)0x00,(byte)0x08,(byte)0x50,
		(byte)0x00,(byte)0x08,(byte)0x6F,(byte)0xF0,(byte)0x3F,(byte)0xC4,(byte)0x10,(byte)0x08,(byte)0x44,(byte)0x30,(byte)0x0A,(byte)0x44,(byte)0x20,(byte)0x1B,(byte)0x42,(byte)0x60,(byte)0x19,(byte)0x41,(byte)0x40,(byte)0x10,(byte)0x41,(byte)0xC0,(byte)0x10,(byte)0x41,(byte)0x80,(byte)0x10,(byte)0x43,(byte)0x60,(byte)0x22,(byte)0x46,(byte)0x38,(byte)0x41,
		(byte)0xC8,(byte)0x1E,(byte)0x40,(byte)0xF0,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"般",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xE0,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0D,(byte)0x1F,(byte)0xC0,(byte)0x7F,(byte)0x80,(byte)0x00,(byte)0x0C,(byte)0x80,(byte)0x08,(byte)0x0C,(byte)0xFC,(byte)0xFC,(byte)0x0C,(byte)0xC8,
		(byte)0x88,(byte)0x0F,(byte)0xC8,(byte)0x88,(byte)0x1C,(byte)0xF8,(byte)0xF8,(byte)0x7C,(byte)0xCE,(byte)0x80,(byte)0x0C,(byte)0x03,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFC,(byte)0x0C,(byte)0x0F,(byte)0x00,(byte)0x0C,(byte)0x0A,(byte)0x80,(byte)0x0C,(byte)0x12,(byte)0x40,(byte)0x0C,(byte)0x22,(byte)0x30,(byte)0x0C,(byte)0x42,(byte)0x1E,(byte)0x6D,
		(byte)0x82,(byte)0x08,(byte)0x1A,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"操",4*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x03,(byte)0x8C,(byte)0x00,(byte)0x03,(byte)0x08,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x00,(byte)0x06,(byte)0x10,(byte)0x08,(byte)0x04,(byte)0x1F,(byte)0xFC,(byte)0x04,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x44,(byte)0x00,(byte)0x14,(byte)0x84,
		(byte)0x00,(byte)0x25,(byte)0x04,(byte)0x18,(byte)0x44,(byte)0x07,(byte)0xE0,(byte)0x44,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x18,(byte)0x04,(byte)0x07,(byte)0xE0,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,
		(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"作",5*/

	};
	public static byte[] buffer6 = {
			// 封袋扫描

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x30,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x20,(byte)0x20,(byte)0x3F,(byte)0xF0,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x24,(byte)0x03,(byte)0x1F,(byte)0xFE,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x30,
		(byte)0x20,(byte)0x3C,(byte)0xCC,(byte)0x20,(byte)0x03,(byte)0x02,(byte)0x20,(byte)0x03,(byte)0x03,(byte)0x20,(byte)0x03,(byte)0x03,(byte)0x20,(byte)0x3F,(byte)0xF1,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x38,(byte)0x20,(byte)0x7F,(byte)0xC0,(byte)0x20,(byte)0x70,
		(byte)0x01,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,/*"封",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0D,(byte)0x80,(byte)0x03,(byte)0x08,(byte)0xC0,(byte)0x06,(byte)0x0C,(byte)0x40,(byte)0x06,(byte)0x04,(byte)0x3C,(byte)0x0B,(byte)0xFF,(byte)0xC0,(byte)0x12,(byte)0x06,(byte)0x00,(byte)0x22,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x88,(byte)0x02,(byte)0x10,
		(byte)0xE8,(byte)0x06,(byte)0x18,(byte)0x3C,(byte)0x06,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xC8,(byte)0x60,(byte)0x01,(byte)0x88,(byte)0xC0,(byte)0x07,(byte)0x85,(byte)0x00,(byte)0x0D,(byte)0x82,(byte)0x00,(byte)0x31,(byte)0x85,(byte)0x80,(byte)0x41,(byte)0xB8,(byte)0xF0,(byte)0x01,
		(byte)0xC0,(byte)0x3E,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"袋",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/

	};
	public static byte[] buffer7 = {
			// 入库扫描
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0xC0,(byte)0x00,(byte)0x00,(byte)0x30,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x34,(byte)0x00,(byte)0x00,(byte)0x24,
		(byte)0x00,(byte)0x00,(byte)0x64,(byte)0x00,(byte)0x00,(byte)0x62,(byte)0x00,(byte)0x00,(byte)0x43,(byte)0x00,(byte)0x00,(byte)0x83,(byte)0x00,(byte)0x01,(byte)0x81,(byte)0x80,(byte)0x01,(byte)0x00,(byte)0xC0,(byte)0x02,(byte)0x00,(byte)0xE0,(byte)0x04,(byte)0x00,(byte)0x70,(byte)0x08,(byte)0x00,(byte)0x38,(byte)0x10,(byte)0x00,(byte)0x1E,(byte)0x20,
		(byte)0x00,(byte)0x08,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"入",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x08,(byte)0x08,(byte)0x00,(byte)0x1C,(byte)0x0F,(byte)0xFF,(byte)0xE0,(byte)0x08,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0x10,(byte)0x00,(byte)0x08,(byte)0x30,(byte)0x18,(byte)0x0B,(byte)0xEF,(byte)0xE0,(byte)0x08,(byte)0x24,
		(byte)0x00,(byte)0x08,(byte)0x46,(byte)0x00,(byte)0x08,(byte)0x46,(byte)0x00,(byte)0x08,(byte)0x86,(byte)0x20,(byte)0x08,(byte)0xFF,(byte)0xC0,(byte)0x18,(byte)0x06,(byte)0x00,(byte)0x10,(byte)0x06,(byte)0x08,(byte)0x10,(byte)0x06,(byte)0x1C,(byte)0x13,(byte)0xFF,(byte)0xE0,(byte)0x10,(byte)0x06,(byte)0x00,(byte)0x20,(byte)0x06,(byte)0x00,(byte)0x20,
		(byte)0x06,(byte)0x00,(byte)0x40,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"库",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/


	};
	public static byte[] buffer8 = {
			// 出库扫描

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x0C,(byte)0x18,(byte)0x30,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,(byte)0x20,(byte)0x0C,(byte)0x18,
		(byte)0x20,(byte)0x0F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0x18,(byte)0x20,(byte)0x00,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0x18,(byte)0x10,(byte)0x08,(byte)0x18,(byte)0x18,(byte)0x08,(byte)0x18,(byte)0x10,(byte)0x08,(byte)0x18,(byte)0x10,(byte)0x08,(byte)0x18,(byte)0x10,(byte)0x08,(byte)0x18,(byte)0x10,(byte)0x08,(byte)0x18,(byte)0x10,(byte)0x0F,
		(byte)0xE7,(byte)0xF0,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x00,/*"出",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x08,(byte)0x08,(byte)0x00,(byte)0x1C,(byte)0x0F,(byte)0xFF,(byte)0xE0,(byte)0x08,(byte)0x18,(byte)0x00,(byte)0x08,(byte)0x10,(byte)0x00,(byte)0x08,(byte)0x30,(byte)0x18,(byte)0x0B,(byte)0xEF,(byte)0xE0,(byte)0x08,(byte)0x24,
		(byte)0x00,(byte)0x08,(byte)0x46,(byte)0x00,(byte)0x08,(byte)0x46,(byte)0x00,(byte)0x08,(byte)0x86,(byte)0x20,(byte)0x08,(byte)0xFF,(byte)0xC0,(byte)0x18,(byte)0x06,(byte)0x00,(byte)0x10,(byte)0x06,(byte)0x08,(byte)0x10,(byte)0x06,(byte)0x1C,(byte)0x13,(byte)0xFF,(byte)0xE0,(byte)0x10,(byte)0x06,(byte)0x00,(byte)0x20,(byte)0x06,(byte)0x00,(byte)0x20,
		(byte)0x06,(byte)0x00,(byte)0x40,(byte)0x06,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"库",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/

	};
	public static byte[] buffer9 = {
			// 验封扫描
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x80,(byte)0x3F,(byte)0x83,(byte)0x80,(byte)0x01,(byte)0x82,(byte)0x80,(byte)0x11,(byte)0x02,(byte)0x40,(byte)0x19,(byte)0x04,(byte)0x40,(byte)0x11,(byte)0x0C,(byte)0x20,(byte)0x11,(byte)0x08,(byte)0x10,(byte)0x11,(byte)0x10,(byte)0x3C,(byte)0x11,(byte)0x3F,
		(byte)0xFC,(byte)0x11,(byte)0x40,(byte)0x00,(byte)0x3F,(byte)0xC0,(byte)0x10,(byte)0x00,(byte)0xC2,(byte)0x18,(byte)0x00,(byte)0x92,(byte)0x18,(byte)0x00,(byte)0x89,(byte)0x10,(byte)0x0F,(byte)0x89,(byte)0x90,(byte)0x70,(byte)0x89,(byte)0xA0,(byte)0x00,(byte)0x89,(byte)0x20,(byte)0x00,(byte)0x88,(byte)0x40,(byte)0x11,(byte)0x80,(byte)0x40,(byte)0x0F,
		(byte)0x00,(byte)0x4C,(byte)0x02,(byte)0x1F,(byte)0xB0,(byte)0x00,(byte)0x00,(byte)0x00,/*"验",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x30,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x20,(byte)0x20,(byte)0x3F,(byte)0xF0,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x24,(byte)0x03,(byte)0x1F,(byte)0xFE,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x30,
		(byte)0x20,(byte)0x3C,(byte)0xCC,(byte)0x20,(byte)0x03,(byte)0x02,(byte)0x20,(byte)0x03,(byte)0x03,(byte)0x20,(byte)0x03,(byte)0x03,(byte)0x20,(byte)0x3F,(byte)0xF1,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x00,(byte)0x20,(byte)0x03,(byte)0x38,(byte)0x20,(byte)0x7F,(byte)0xC0,(byte)0x20,(byte)0x70,
		(byte)0x01,(byte)0xE0,(byte)0x00,(byte)0x00,(byte)0x40,(byte)0x00,(byte)0x00,(byte)0x00,/*"封",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/



	};
	public static byte[] buffer10 = {
			// 开袋扫描
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x10,(byte)0x00,(byte)0x00,(byte)0x38,(byte)0x1F,(byte)0xFF,(byte)0xC0,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,
		(byte)0x0C,(byte)0x7F,(byte)0xFF,(byte)0xFE,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x00,(byte)0x81,(byte)0x00,(byte)0x01,(byte)0x81,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x00,(byte)0x01,(byte)0x01,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x00,(byte)0x06,(byte)0x01,(byte)0x00,(byte)0x0C,(byte)0x01,(byte)0x00,(byte)0x10,
		(byte)0x01,(byte)0x00,(byte)0x20,(byte)0x01,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,/*"开",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0D,(byte)0x80,(byte)0x03,(byte)0x08,(byte)0xC0,(byte)0x06,(byte)0x0C,(byte)0x40,(byte)0x06,(byte)0x04,(byte)0x3C,(byte)0x0B,(byte)0xFF,(byte)0xC0,(byte)0x12,(byte)0x06,(byte)0x00,(byte)0x22,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x88,(byte)0x02,(byte)0x10,
		(byte)0xE8,(byte)0x06,(byte)0x18,(byte)0x3C,(byte)0x06,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xC8,(byte)0x60,(byte)0x01,(byte)0x88,(byte)0xC0,(byte)0x07,(byte)0x85,(byte)0x00,(byte)0x0D,(byte)0x82,(byte)0x00,(byte)0x31,(byte)0x85,(byte)0x80,(byte)0x41,(byte)0xB8,(byte)0xF0,(byte)0x01,
		(byte)0xC0,(byte)0x3E,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"袋",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x03,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x02,(byte)0x00,(byte)0x0C,(byte)0x02,(byte)0xFF,(byte)0xF8,(byte)0x3F,(byte)0xE0,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x20,
		(byte)0x08,(byte)0x02,(byte)0xC0,(byte)0x08,(byte)0x07,(byte)0x1F,(byte)0xF8,(byte)0x3A,(byte)0x00,(byte)0x08,(byte)0x22,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x00,(byte)0x08,(byte)0x02,(byte)0x3F,(byte)0xF8,(byte)0x1E,
		(byte)0x00,(byte)0x08,(byte)0x04,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"扫",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x06,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x4C,(byte)0x04,(byte)0xFF,(byte)0xF0,(byte)0x7F,(byte)0xC4,(byte)0x40,(byte)0x04,(byte)0x04,(byte)0x60,(byte)0x04,(byte)0x04,(byte)0x40,(byte)0x04,(byte)0x00,
		(byte)0x08,(byte)0x04,(byte)0xFF,(byte)0xFC,(byte)0x07,(byte)0x21,(byte)0x08,(byte)0x1C,(byte)0x21,(byte)0x08,(byte)0x74,(byte)0x21,(byte)0x08,(byte)0x24,(byte)0x3F,(byte)0xF8,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x04,(byte)0x21,(byte)0x08,(byte)0x1C,
		(byte)0x3E,(byte)0xF8,(byte)0x0C,(byte)0x20,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"描",3*/




	};
	public static byte[] buffer11 = {
			// 换袋操作
		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x06,(byte)0x0E,(byte)0x00,(byte)0x04,(byte)0x08,(byte)0x00,(byte)0x04,(byte)0x1F,(byte)0xC0,(byte)0x04,(byte)0x10,(byte)0xC0,(byte)0x05,(byte)0xA0,(byte)0x80,(byte)0x3E,(byte)0x61,(byte)0x00,(byte)0x04,(byte)0x62,(byte)0x10,(byte)0x04,(byte)0xBF,(byte)0xF0,(byte)0x04,(byte)0x22,
		(byte)0x10,(byte)0x05,(byte)0xA2,(byte)0x10,(byte)0x06,(byte)0x22,(byte)0x10,(byte)0x3C,(byte)0x22,(byte)0x10,(byte)0x24,(byte)0x22,(byte)0x14,(byte)0x04,(byte)0xDF,(byte)0xE8,(byte)0x04,(byte)0x07,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x80,(byte)0x04,(byte)0x0C,(byte)0x40,(byte)0x04,(byte)0x08,(byte)0x60,(byte)0x04,(byte)0x10,(byte)0x30,(byte)0x1C,
		(byte)0x60,(byte)0x1E,(byte)0x08,(byte)0x80,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,/*"换",0*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x01,(byte)0x0D,(byte)0x80,(byte)0x03,(byte)0x08,(byte)0xC0,(byte)0x06,(byte)0x0C,(byte)0x40,(byte)0x06,(byte)0x04,(byte)0x3C,(byte)0x0B,(byte)0xFF,(byte)0xC0,(byte)0x12,(byte)0x06,(byte)0x00,(byte)0x22,(byte)0x02,(byte)0x00,(byte)0x02,(byte)0x01,(byte)0x88,(byte)0x02,(byte)0x10,
		(byte)0xE8,(byte)0x06,(byte)0x18,(byte)0x3C,(byte)0x06,(byte)0x08,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xFF,(byte)0xF0,(byte)0x00,(byte)0xC8,(byte)0x60,(byte)0x01,(byte)0x88,(byte)0xC0,(byte)0x07,(byte)0x85,(byte)0x00,(byte)0x0D,(byte)0x82,(byte)0x00,(byte)0x31,(byte)0x85,(byte)0x80,(byte)0x41,(byte)0xB8,(byte)0xF0,(byte)0x01,
		(byte)0xC0,(byte)0x3E,(byte)0x00,(byte)0x80,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"袋",1*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x00,(byte)0x00,(byte)0x0C,(byte)0x1F,(byte)0xE0,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0C,(byte)0x10,(byte)0x40,(byte)0x0D,(byte)0x1F,(byte)0xC0,(byte)0x7F,(byte)0x80,(byte)0x00,(byte)0x0C,(byte)0x80,(byte)0x08,(byte)0x0C,(byte)0xFC,(byte)0xFC,(byte)0x0C,(byte)0xC8,
		(byte)0x88,(byte)0x0F,(byte)0xC8,(byte)0x88,(byte)0x1C,(byte)0xF8,(byte)0xF8,(byte)0x7C,(byte)0xCE,(byte)0x80,(byte)0x0C,(byte)0x03,(byte)0x00,(byte)0x0F,(byte)0xFF,(byte)0xFC,(byte)0x0C,(byte)0x0F,(byte)0x00,(byte)0x0C,(byte)0x0A,(byte)0x80,(byte)0x0C,(byte)0x12,(byte)0x40,(byte)0x0C,(byte)0x22,(byte)0x30,(byte)0x0C,(byte)0x42,(byte)0x1E,(byte)0x6D,
		(byte)0x82,(byte)0x08,(byte)0x1A,(byte)0x02,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"操",2*/

		(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x08,(byte)0x00,(byte)0x03,(byte)0x8C,(byte)0x00,(byte)0x03,(byte)0x08,(byte)0x00,(byte)0x02,(byte)0x18,(byte)0x00,(byte)0x06,(byte)0x10,(byte)0x08,(byte)0x04,(byte)0x1F,(byte)0xFC,(byte)0x04,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x24,(byte)0x00,(byte)0x0C,(byte)0x44,(byte)0x00,(byte)0x14,(byte)0x84,
		(byte)0x00,(byte)0x25,(byte)0x04,(byte)0x18,(byte)0x44,(byte)0x07,(byte)0xE0,(byte)0x44,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x18,(byte)0x04,(byte)0x07,(byte)0xE0,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x04,
		(byte)0x04,(byte)0x00,(byte)0x04,(byte)0x04,(byte)0x00,(byte)0x00,(byte)0x00,(byte)0x00,/*"作",3*/


	};
}
