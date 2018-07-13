package com.powerwin.entity;

import java.util.Arrays;

public class CharArray implements CharSequence {
	public char[] chars;
	
	public CharArray(char[] chars) {
		this.chars = chars;
	}
	
	public CharArray(char[] chars, int offset, int end) {
		this.chars = Arrays.copyOfRange(chars, offset, end);
	}
	
	public int length() {
		return this.chars.length;
	}

	public char charAt(int index) {
		return chars[index];
	}

	public CharSequence subSequence(int start, int end) {
		if(end > this.chars.length || start > this.chars.length || start < 0) return null;
		return new CharArray(chars, start, end);
	}
}
