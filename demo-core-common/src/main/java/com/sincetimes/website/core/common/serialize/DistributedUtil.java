package com.sincetimes.website.core.common.support;

import static com.sincetimes.website.core.common.support.WireFormat.ARRAY;
import static com.sincetimes.website.core.common.support.WireFormat.BOOLEAN;
import static com.sincetimes.website.core.common.support.WireFormat.BYTE;
import static com.sincetimes.website.core.common.support.WireFormat.COLLECTION;
import static com.sincetimes.website.core.common.support.WireFormat.DISTRIBUTED;
import static com.sincetimes.website.core.common.support.WireFormat.DOUBLE;
import static com.sincetimes.website.core.common.support.WireFormat.ENUM;
import static com.sincetimes.website.core.common.support.WireFormat.FLOAT;
import static com.sincetimes.website.core.common.support.WireFormat.INT;
import static com.sincetimes.website.core.common.support.WireFormat.LIST;
import static com.sincetimes.website.core.common.support.WireFormat.LONG;
import static com.sincetimes.website.core.common.support.WireFormat.MAP;
import static com.sincetimes.website.core.common.support.WireFormat.MSG;
import static com.sincetimes.website.core.common.support.WireFormat.NULL;
import static com.sincetimes.website.core.common.support.WireFormat.OBJECT;
import static com.sincetimes.website.core.common.support.WireFormat.SET;
import static com.sincetimes.website.core.common.support.WireFormat.STRING;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.sincetimes.website.core.common.core.SysException;
import com.sincetimes.website.core.common.core.interfaces.ISerilizable;
/**
 * 支持实现{@link ISerilizable}的类,<br>
 * 以及<b>byte,float,double,long</b>四种基本类型,<br>和
 * <b>Collection(list和set),map</b>三种集合框架,<br>和
 * <b>protobuf</b>.
 * <br>
 * TODO:循环因引用
 * @author BAO
 * @see #readFile(String fileName) 创建关闭FileInputStream DataInputStream
 * @see #read(DataInputStream in)
 * @see #readObject(DataInputStream stream) throws Exception
 * 
 */
public class SerializeTool {

	/**
	 * 从流中读取数据
	 * 只能顺序读 会自动进行类型转换
	 * @return
	 */
	public static <T> T read(DataInputStream stream) {
		try {
			return readObject(stream);
		} catch (Exception e) {
			LogCore.BASE.error("read err!", e);
			throw new SysException(e);
		}
	}
	
	/**
	 * 从流中读取数据
	 * 只能顺序读 会自动进行类型转换
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> T readObject(DataInputStream stream) throws Exception {
		Object result = null;
		
		//类型码
		int wireFormat = stream.readInt();
		//类型
		int wireType = (wireFormat & ~ARRAY);
		//是数组类型
		boolean isArray = (wireFormat & ARRAY) == ARRAY;
		//数组类型的长度
		int arrayLen = 0;
		if(isArray) {
			arrayLen = stream.readInt();
		}
		
		//空对象
		if(wireType == NULL) {
			return null;
		}
		
		//BYTE
		if(wireType == BYTE) {
			if(isArray) {
				//result = stream.readRawBytes(arrayLen);//TODO 这个方法要调研一下! API应该提供此方法
				byte[] values = new byte[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readByte();
				}
				result = values;
			} else {
				result = stream.readByte();
			}
		//BOOLEAN
		} else if(wireType == BOOLEAN) {
			if(isArray) {
				boolean[] values = new boolean[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readBoolean();
				}
				result = values;
			} else {
				result = stream.readBoolean();
			}
		//INT
		} else if(wireType == INT) {
			if(isArray) {
				int[] values = new int[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readInt();
				}
				result = values;
			} else {
				result = stream.readInt();
			}
		//LONG
		} else if(wireType == LONG) {
			if(isArray) {
				long[] values = new long[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readLong();
				}
				result = values;
			} else {
				result = stream.readLong();
			}
		//FLOAT
		} else if(wireType == FLOAT) {
			if(isArray) {
				float[] values = new float[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readFloat();
				}
				result = values;
			} else {
				result = stream.readFloat();
			}
		//DOUBLE
		} else if(wireType == DOUBLE) {
			if(isArray) {
				double[] values = new double[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readDouble();
				}
				result = values;
			} else {
				result = stream.readDouble();
			}
		//STRING
		} else if(wireType == STRING) {
			if(isArray) {
				String[] values = new String[arrayLen];
				for(int i = 0; i < arrayLen; i++) {
					values[i] = stream.readUTF();
				}
				result = values;
			} else {
				result = stream.readUTF();
			}
		//ENUM
		} else if(wireType == ENUM) {
			//实际类型
			String className = stream.readUTF();
			String val = stream.readUTF();
			
			//创建实例
			Class cls = Class.forName(className);
			result = Enum.valueOf(cls, val);
		
		//COLLECTION LIST SET
		} else if(wireType == COLLECTION || wireType == LIST || wireType == SET) {
			//长度
			int len = stream.readInt();
			
			//类型
			Collection list;
			if(wireType == LIST) list = new ArrayList<>();
			else if(wireType == SET) list = new HashSet<>();
			else list = new ArrayList<>();	//未知Collection的具体实现 暂时一律使用arrayList子类的实现
			
			//填充数据
			for(int i = 0; i < len; i++) {
				list.add(read(stream));
			}
			result = list;
						
		//MAP
		} else if(wireType == MAP) {
			//长度
			int len = stream.readInt();
			
			//数据
			Map map = new LinkedHashMap<>();
			for(int i = 0; i < len; i++) {
				Object key = read(stream);
				Object val = read(stream);
				
				map.put(key, val);
			}
			
			result = map;
		
		//IDistributedSerilizable接口
		} else if(wireType == DISTRIBUTED) {
			//实际类型
			/*这种方式要提前生成常量池switch case(int)
			int id = stream.readInt();
			ISerilizable seriable = org.gof.core.CommonSerializer.create(id);
			if(seriable == null)
				seriable = _commonFunc.apply(id);
			seriable.readFrom(this);
			result = seriable;*/
			
			
			String className = stream.readUTF();
			Class<?> cls = Class.forName(className);
			
			//创建实例并加载数据
	    	Constructor<?> constructor = cls.getDeclaredConstructor();
	    	constructor.setAccessible(true);
			ISerilizable seriable = (ISerilizable)constructor.newInstance();
			seriable.readFrom(stream);
			result = seriable;
		//protobuf消息
		} else if(wireType == MSG) {
			
		//Object[]
		} else if(wireType == OBJECT && isArray) {
			Object[] values = new Object[arrayLen];
			for(int i = 0; i < arrayLen; i++) {
				values[i] = read(stream);
			}
			result = values;
		
		//其余一律不支持
		} else {
			throw new SysException("发现无法被反序列化的类型: wireType={}, isArray={}", wireType, isArray);
		}
		
		//返回值
		return (T) result;
	}
	/*write*/
	
	public static <T> T deserilize(byte[] data){
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		DataInputStream stream = new DataInputStream(in);
		return read(stream);
	}
	/**
	 * 连续读取Object到list
	 */
	public static <T> List<T> deserilize2List(byte[] data){
		return deserilize2List(data, new ArrayList<>());
	}
	public static <T> List<T> deserilize2List(byte[] data, List<T> list){
		try (ByteArrayInputStream in = new ByteArrayInputStream(data);
				DataInputStream stream = new DataInputStream(in)){
			while(stream.available() > 0){
				T t = read(stream);
				list.add(t);
			}
			return list;
		} catch (Exception e) {
			LogCore.BASE.error("read file err:{}", e);
			return null;
		}
	}
	public static byte[] serlize(Object value) throws IOException {
		try(ByteArrayOutputStream bout = new ByteArrayOutputStream();
			DataOutputStream stream = new DataOutputStream(bout)) {
			writeObject(value, stream);
			return bout.toByteArray();
		} catch (Exception e) {
			throw new SysException(e, "OutputStream写入数据失败。");
		}
	}
	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param value
	 * @throws IOException
	 */
	public static void write(Object value, DataOutputStream stream) {
		try {
			writeObject(value, stream);
		//不支持序列化化的错误 要对外汇报
		} catch (Exception e) {
			throw new SysException(e, "OutputStream写入数据失败。");
		}
	}

	/**
	 * 写入数据到流中
	 * 仅支持
	 * byte byte[] boolean boolean[] int int[] long long[] 
	 * double double[] String String[] 
	 * Enum枚举 List、Map两种包装类型
	 * 以及实现了IDistributedSerilizable接口的类
	 * @param value
	 * @throws IOException
	 */
	static void writeObject(Object value, DataOutputStream stream) throws IOException {
		//空对象
		if(value == null) {
			stream.writeInt(NULL);
		}
		
		//数据类型
		Class<?> clazz = value.getClass();

		//BYTE
		if(clazz == byte.class || clazz == Byte.class) {
			stream.writeInt(BYTE);
			stream.writeByte((byte)value);
		} else if(clazz == byte[].class) {
			byte[] array = (byte[])value;
			stream.writeInt(BYTE | ARRAY);
			stream.writeInt(array.length);
			stream.write(array);
		//BOOLEAN
		} else if(clazz == boolean.class || clazz == Boolean.class) {
			stream.writeInt(BOOLEAN);
			stream.writeBoolean((boolean)value);
		} else if(clazz == boolean[].class) {
			boolean[] array = (boolean[])value;
			stream.writeInt(BOOLEAN | ARRAY);
			stream.writeInt(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeBoolean(array[i]);
			}
		//INT
		} else if(clazz == int.class || clazz == Integer.class) {
			stream.writeInt(INT);
			stream.writeInt((int)value);
		} else if(clazz == int[].class) {
			int[] array = (int[])value;
			stream.writeInt(INT | ARRAY);
			stream.writeInt(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeInt(array[i]);
			}
		//LONG
		} else if(clazz == long.class || clazz == Long.class) {
			stream.writeInt(LONG);
			stream.writeLong((long)value);
		} else if(clazz == long[].class) {
			long[] array = (long[])value;
			stream.writeInt(LONG | ARRAY);
			stream.writeInt(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeLong(array[i]);
			}
		//FLOAT
		} else if(clazz == float.class || clazz == Float.class) {
			stream.writeFloat(FLOAT);
			stream.writeFloat((float)value);
		} else if(clazz == float[].class) {
			float[] array = (float[])value;
			stream.writeInt(FLOAT | ARRAY);
			stream.writeInt(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeFloat(array[i]);
			}
		//DOUBLE
		} else if(clazz == double.class || clazz == Double.class) {
			stream.writeInt(DOUBLE);
			stream.writeDouble((double)value);
		} else if(clazz == double[].class) {
			double[] array = (double[])value;
			stream.writeInt(DOUBLE | ARRAY);
			stream.writeInt(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeDouble(array[i]);
			}
		//STRING
		} else if(clazz == String.class) {
			stream.writeInt(STRING);
			stream.writeUTF((String)value);
		} else if(clazz == String[].class) {
			String[] array = (String[])value;
			stream.writeInt(STRING | ARRAY);
			stream.writeInt(array.length);
			for(int i = 0; i < array.length; i++) {
				stream.writeUTF(array[i]);
			}
		//ENUM
		} else if(value instanceof Enum) {
			Enum<?> val = (Enum<?>) value;
			stream.writeInt(ENUM);
			stream.writeUTF(val.getClass().getName());
			stream.writeUTF(val.name());
			
		//COLLECTION LIST SET
		} else if(value instanceof Collection) {
			Collection<?> val = (Collection<?>) value;
			
			//判断子类型
			int type;
			if(value instanceof List) type = LIST;
			else if(value instanceof Set) type = SET;
			else type = COLLECTION;
			
			stream.writeInt(type);
			stream.writeInt(val.size());
			
			for(Object o : val) {
				write(o, stream);
			}
		
		//MAP
		} else if(value instanceof Map) {
			Map<?,?> val = (Map<?,?>) value;
			
			stream.writeInt(MAP);
			stream.writeInt(val.size());
			
			for(Entry<?, ?> e : val.entrySet()) {
				Object k = e.getKey();
				Object v = e.getValue();
				write(k, stream);
				write(v, stream);
			}
						
		//IDistributedSerilizable接口
		} else if(value instanceof ISerilizable) {
			ISerilizable seriable = (ISerilizable)value;
			stream.writeInt(DISTRIBUTED);
			stream.writeInt(value.getClass().getName().hashCode());
			seriable.writeTo(stream);
		//protobuf消息
//		} else if(value instanceof Message) {
//			Message msg = (Message)value;
//			byte[] bytes = msg.toByteArray();
//			
//			stream.writeInt32NoTag(MSG);
//			stream.writeInt32NoTag(bytes.length);		//消息长度 不包括消息类型
//			stream.writeInt32NoTag(msg.getClass().getName().hashCode());
//			stream.writeRawBytes(bytes);
			
		//数组
		} else if(value instanceof Object[]) {
			Object[] array = (Object[])value;
			stream.writeInt(OBJECT | ARRAY);
			stream.writeInt(array.length);
			for(Object o : array) {
				write(o, stream);
			}
		//其余一律不支持
		} else {
			throw new SysException("发现无法被序列化的类型:{}", clazz.getName());
		}
	}
}
