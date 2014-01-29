package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.AbstractListModel;

public class HighscoreSet extends AbstractListModel<Highscore>  {
	
	private List<Highscore> highscores;
	
	public HighscoreSet() {
		this.highscores = new ArrayList<>();
	}
	
	public void addHighScore(Highscore highscore) {
		this.highscores.add(highscore);
		Collections.sort(highscores);
	}
	
	public Highscore getBestScore() {
		if(this.highscores.size() <= 0) return null;
		return this.highscores.get(0);
	}
	
	@Override
	public int getSize() {
		return highscores.size();
	}

	@Override
	public Highscore getElementAt(int index) {
		// TODO Auto-generated method stub
		return highscores.get(index);
	}
	
}
