package com.cjm.gmall.service.impl;

import java.util.Arrays;
import java.util.List;

import com.cjm.gmall.bean.UserAddress;
import com.cjm.gmall.service.UserService;

/**
 * 1���������ṩ��ע�ᵽע������
 * 		1������dubbo������2.6.2��������zookeeper�Ŀͻ�������(curator)
 * 		2�����÷����ṩ��
 * 2���÷���������ȥע�����Ķ��ķ����ṩ�ߵķ����ַ
 * @author �¼���
 *
 */
public class UserServiceImpl implements UserService {

	public List<UserAddress> getUserAddressList(String userId) {
		System.out.println("UserServiceImpl.....old...");
		// TODO Auto-generated method stub
		UserAddress address1 = new UserAddress(1, "�����в�ƽ���긣�Ƽ�԰�ۺ�¥3��", "1", "����ʦ", "010-56253825", "Y");
		UserAddress address2 = new UserAddress(2, "�����б�����������ȴ���B��3�㣨���ڷ�У��", "1", "����ʦ", "010-56253825", "N");
		/*try {
			Thread.sleep(4000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		return Arrays.asList(address1,address2);
	}

}
