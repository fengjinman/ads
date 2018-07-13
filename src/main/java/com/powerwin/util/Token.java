package com.powerwin.util;

public class Token<T> {
	public int begin;
	public int end;
	public T   data;
	
	Token(int begin, int end, T data) {
		this.begin = begin;
		this.end = end;
		this.data = data;
	}
}