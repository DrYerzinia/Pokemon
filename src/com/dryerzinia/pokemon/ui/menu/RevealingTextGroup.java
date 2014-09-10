package com.dryerzinia.pokemon.ui.menu;

import java.util.ArrayList;
import java.util.List;

/**
 * Group of RevealingText objects. 
 * Represents a full sentence in the text box split over several lines.
 * 
 * @author jc
 *
 */
public class RevealingTextGroup {
	private List<RevealingText> lines;
	private int index = 0;

	public RevealingTextGroup() {
		lines = new ArrayList<RevealingText>();
	}

	public boolean hasNextLine() {
		return index < lines.size();
	}

	public RevealingText getNextLine() {
		return lines.get(index++);
	}

	public void add(RevealingText line) {
		lines.add(line);
	}

	/**
	 * Represents a portion of a sentence that can be revealed
	 * one character at a time.
	 * 
	 * @author jc
	 *
	 */
	class RevealingText {
		private String currentText, targetText;
		private int nCharsRevealed;

		public RevealingText(String targetText) {
			currentText = "";
			this.targetText = targetText;
		}

		/**
		 * Reveal one more character unless already fully revealed.
		 */
		public void revealCharacter() {
			currentText = targetText.substring(0, nCharsRevealed++);
			if (nCharsRevealed > targetText.length()) {
				nCharsRevealed = targetText.length();
			}
		}

		public boolean isRevealed() {
			return currentText.equals(targetText);
		}

		public String getCurrentText() {
			return currentText;
		}

		public String getTargetText() {
			return targetText;
		}
	}
}
