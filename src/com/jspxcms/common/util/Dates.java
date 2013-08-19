package com.jspxcms.common.util;

import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;

/**
 * 日期工具类
 * 
 * @author liufang
 * 
 */
public abstract class Dates {
	public static Date parse(String s) {
		return parse(s, false);
	}

	public static Date parseEndDate(String s) {
		return parse(s, true);
	}

	public static Date parse(String s, boolean endDate) {
		if (StringUtils.isBlank(s)) {
			return null;
		}
		s = s.trim();
		DateTime dt;
		if (s.startsWith("now")) {
			dt = DateTime.now();
			String[] arr = StringUtils.split(s, ',');
			int num;
			boolean plus;
			String[] opts;
			for (int i = 1, len = arr.length; i < len; i++) {
				plus = true;
				opts = StringUtils.split(arr[i], '+');
				if (opts.length != 2) {
					plus = false;
					opts = StringUtils.split(arr[i], '-');
				}
				if (opts.length != 2) {
					continue;
				}
				num = NumberUtils.toInt(opts[1], 0);
				if (!plus) {
					num = -num;
				}
				if (opts[0].equalsIgnoreCase("year")) {
					dt = dt.plusYears(num);
				} else if (opts[0].equalsIgnoreCase("month")) {
					dt = dt.plusMonths(num);
				} else if (opts[0].equalsIgnoreCase("day")) {
					dt = dt.plusDays(num);
				} else if (opts[0].equalsIgnoreCase("hour")) {
					dt = dt.plusHours(num);
				} else if (opts[0].equalsIgnoreCase("minute")) {
					dt = dt.plusMinutes(num);
				} else if (opts[0].equalsIgnoreCase("second")) {
					dt = dt.plusSeconds(num);
				}
			}
		} else {
			dt = DateTime.parse(s);
			if (endDate && s.length() <= 10) {
				dt = dt.plusDays(1);
				dt = dt.minusMillis(1);
			}
		}
		return dt.toDate();
	}
}
