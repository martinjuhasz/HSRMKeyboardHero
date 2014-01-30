/**
 * 
 * 
 * @author Simon Seyer
 * @author Martin Juhasz
 * @author Julia Kraft
 * @author Moritz Moeller
 * 
 */
package view;

import helper.KeyboardHeroConstants;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import controller.PlayerController;
import model.Highscore;
import model.Score;
import model.StrokeSet;
import net.miginfocom.swing.MigLayout;

public class GameResultsPanel extends JPanel {

	private PlayerController playerController;
	private JTextField highscoreNameField;
	private JButton highscoreSubmitButton;
	private JLabel highscoreTitleLabel;
	private JLabel scoreLabel;
	private JPanel infoPanel;

	private ResultListener listener;

	public interface ResultListener {
		
		/**
		 * Result panel should close.
		 */
		public void resultPanelShouldClose();

		/**
		 * Result panel did select replay.
		 */
		public void resultPanelDidSelectReplay();
	}
	
	/**
	 * Instantiates a new game results panel.
	 *
	 * @param playerController the player controller
	 */
	public GameResultsPanel(PlayerController playerController) {
		this.playerController = playerController;
	}

	/**
	 * Instantiates a new game results panel.
	 *
	 * @param aListener the a listener
	 */
	public GameResultsPanel(PlayerController playerController, ResultListener aListener) {
		this(playerController);
		this.listener = aListener;
		
		this.setBackground(new Color(0,0,0, 170));
		this.setLayout(new MigLayout("fill","[center]","[center]"));
		
		infoPanel = new JPanel();
		infoPanel.setLayout(new MigLayout());
		infoPanel.setBackground(Color.WHITE);
		this.add(infoPanel, "w 500!, h 500!");
		
		if(!playerController.isRecording()){
			setupViewsForPlaying();
		} else {
			setupViewForRecording();
		}

		JPanel pushPanel = new JPanel();
		pushPanel.setBackground(Color.WHITE);
		infoPanel.add(pushPanel, "grow, pushy, span");

		String playButtonText = KeyboardHeroConstants.getString("play_again");
		if (playerController.isRecording()) {
			playButtonText = KeyboardHeroConstants.getString("record_again");
		}

		JButton playButton = new MenuButton(playButtonText, new Color(
				KeyboardHeroConstants.FONT_COLOR_SECONDARY));
		playButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.resultPanelDidSelectReplay();
			}
		});
		infoPanel.add(playButton, "w 45%");

		JButton closeButton = new MenuButton(
				KeyboardHeroConstants.getString("back_to_menu"), new Color(
						KeyboardHeroConstants.FONT_COLOR_SECONDARY));
		closeButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				listener.resultPanelShouldClose();
			}
		});
		infoPanel.add(closeButton, "w 45%");
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setVisible(boolean)
	 */
	public void setVisible(boolean flag) {
		super.setVisible(flag);

		if (flag == true) {
			viewDidBecomeVisible();
		}
	}

	/**
	 * View did become visible.
	 */
	private void viewDidBecomeVisible() {
		if (!playerController.isRecording()) {
			int score = (int) playerController
					.getScoreController().getScore().getScore();
			scoreLabel.setText(String.valueOf(score));
		}
	}

	/**
	 * Setup view for recording.
	 */
	private void setupViewForRecording() {
		JLabel textLabel = new JLabel(
				KeyboardHeroConstants.getString("recording_successfull"));
		textLabel.setFont(new Font("sanserif", Font.BOLD, 16));
		textLabel.setVerticalAlignment(SwingConstants.CENTER);
		infoPanel.add(textLabel, "span, wrap");
	}

	/**
	 * Setup views for playing.
	 */
	private void setupViewsForPlaying() {
		JLabel scoreTitleLabel = new JLabel(
				KeyboardHeroConstants.getString("your_score_label"));
		scoreTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoPanel.add(scoreTitleLabel, "w 45%");

		JLabel bestScoreTitleLabel = new JLabel(
				KeyboardHeroConstants.getString("best_score_label"));
		bestScoreTitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		infoPanel.add(bestScoreTitleLabel, "w 45%, wrap");

		scoreLabel = new JLabel("0");
		scoreLabel.setOpaque(true);
		scoreLabel.setBackground(new Color(
				KeyboardHeroConstants.FONT_COLOR_PRIMARY));
		scoreLabel.setForeground(Color.WHITE);
		scoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		scoreLabel.setFont(new Font("sanserif", Font.BOLD, 27));
		infoPanel.add(scoreLabel, "w 45%, h 100!");

		Highscore bestHighscore = playerController.getTrack()
				.getStrokeSet().getHighscores().getBestScore();
		String bestScore = "n/a";
		if (bestHighscore != null) {
			bestScore = MessageFormat.format(
					KeyboardHeroConstants.getString("best_score_text"),
					bestHighscore.getScore(), bestHighscore.getName());
		}

		JLabel bestScoreLabel = new JLabel(bestScore);
		bestScoreLabel.setOpaque(true);
		bestScoreLabel.setBackground(new Color(
				KeyboardHeroConstants.FONT_COLOR_PRIMARY));
		bestScoreLabel.setForeground(Color.WHITE);
		bestScoreLabel.setHorizontalAlignment(SwingConstants.CENTER);
		bestScoreLabel.setFont(new Font("sanserif", Font.BOLD, 27));
		infoPanel.add(bestScoreLabel, "w 45%, h 100!, wrap");

		highscoreTitleLabel = new JLabel(
				KeyboardHeroConstants.getString("submit_highscore_label"));
		infoPanel.add(highscoreTitleLabel, "gapy 30,span, wrap");

		highscoreNameField = new JTextField();
		infoPanel.add(highscoreNameField, "w 45%");

		highscoreSubmitButton = new MenuButton(
				KeyboardHeroConstants.getString("submit_highscore"));
		infoPanel.add(highscoreSubmitButton, "w 45%, wrap");
		highscoreSubmitButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (highscoreNameField.getText().trim().length() <= 0)
					return;

				Score score = playerController
						.getScoreController().getScore();
				StrokeSet strokeSet = playerController.getTrack()
						.getStrokeSet();

				strokeSet.addHighscore((int) score.getScore(),
						highscoreNameField.getText());

				highscoreNameField.setVisible(false);
				highscoreSubmitButton.setVisible(false);
				highscoreNameField.setEditable(false);
				highscoreSubmitButton.setEnabled(false);
				highscoreTitleLabel.setText(KeyboardHeroConstants
						.getString("submit_highscore_successfull"));

			}
		});
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		g.setColor(getBackground());
		g.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g);
	}

}
