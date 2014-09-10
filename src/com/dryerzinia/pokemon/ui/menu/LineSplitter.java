// TODO: Implement support for forced newlines in strings
// This happens when an item name is too long to fit on the line.
// You want the \n
// HELIX FOSSIL?


package com.dryerzinia.pokemon.ui.menu;

import java.awt.Canvas;
import java.awt.FontMetrics;

import com.dryerzinia.pokemon.util.ResourceLoader;

public class LineSplitter {
	private static final int MAXWIDTH = 235;
	private static FontMetrics metrics;
	static {
		Canvas c = new Canvas();
		metrics = c.getFontMetrics(Menu.menuFont);
	}
	
	private LineSplitter() {
		// no instance
	}
	
	public static RevealingTextGroup split(String text) {
		RevealingTextGroup splitLines = new RevealingTextGroup();
		String remainingText = text;
		
		while (!"".equals(remainingText)) {
			String result = "";
			int currentWidth = 0;
			
			while (!"".equals(remainingText) && currentWidth <= MAXWIDTH) {
				String nextWord;
				int wordBoundary = remainingText.indexOf(" ");
				if (wordBoundary < 0)
					nextWord = remainingText;
				else
					nextWord = remainingText.substring(0, wordBoundary);
				
				int wordWidth = metrics.stringWidth(nextWord);
				if (currentWidth + wordWidth <= MAXWIDTH) {
					result += nextWord + " ";
					if (wordBoundary < 0)
						remainingText = "";
					else
						remainingText = remainingText.substring(wordBoundary + 1);
					
					currentWidth += wordWidth;
				} else
					break;
			}
			RevealingTextGroup.RevealingText splitLine =
					splitLines.new RevealingText(result.trim());
			splitLines.add(splitLine);
			
		}
		return splitLines;
	}
}
