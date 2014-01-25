package com.dryerzinia.pokemon.util;

public class StringStream {

	private String string;
	private int position;

	public StringStream(String string){

		this.string = string;
		position = 0;

	}

	public char read(){

		return string.charAt(position++);

	}

	public String readUntil(String until){

		StringBuilder stringBuilder = new StringBuilder();

		char c = string.charAt(position);

		while(true){

			for(int i = 0; i < until.length(); i++)
				if(c == until.charAt(i)) return stringBuilder.toString();

			stringBuilder.append(c);
			c = string.charAt(++position);

		}

	}

	public char peek(){

		return string.charAt(position);

	}

	public void ignore(){

		position++;

	}

	public void ignoreUntil(String until){

		char c = string.charAt(position);

		while(true){

			for(int i = 0; i < until.length(); i++)
				if(c == until.charAt(i)) return;

			c = string.charAt(++position);

		}

	}

	public void toNext(char next){

		position = string.indexOf(next, position);

	}

	public void skipWhitespace(){
	
		char c = string.charAt(position);

		while(c == ' ' || c == '\n' || c == '\t'){

			c = string.charAt(++position);

		}

	}

}
