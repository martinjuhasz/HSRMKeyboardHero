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
import java.awt.RenderingHints.Key;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.DropMode;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import view.TextPrompt.Show;
import model.MenuSongList;
import model.Track;
import net.miginfocom.swing.MigLayout;
import controller.PersistenceHandler;
import controller.PlayerController;
import controller.PlaylistTransferHandler;
import controller.player.Playlist;

public class SongListPanel extends GHPanel {

	public static final int MODE_PLAY = 0;
	public static final int MODE_RECORD = 1;
	public static final int MODE_HIGHSCORE = 2;

	private Playlist playlist;
	private JList<Track> songlist;
	private JButton mainMenuButton;
	private ListAction selectAction;
	private PlaylistTransferHandler transferHandler;
	private JScrollPane scrollPane;
	private int mode;
	private JTextField searchField;
	private JButton button;
	private TextPrompt textPrompt;

	/**
	 * Instantiates a new song list panel.
	 */
	public SongListPanel() {
		this.mode = MODE_PLAY;
		init();
	}

	/**
	 * Instantiates a new song list panel.
	 *
	 * @param mode the mode
	 */
	public SongListPanel(int mode) {
		this.mode = mode;
		init();
	}

	/**
	 * Inits the.
	 */
	public void init() {
		this.setLayout(new MigLayout("insets 50 200 50 200, fill"));
		this.setBackground(Color.WHITE);

		JLabel titleLabel = new TitleLabel(
				KeyboardHeroConstants.getString("select_track"));
		this.add(titleLabel, "wrap, grow");

		if (mode == MODE_RECORD) {
			initSearchField();
		}
		initPlaylist();

		selectAction = new ListAction(songlist, new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent e) {
				PlayerController.getInstance().stop();
				if (songlist.getSelectedValue() != null) {
					if (mode == MODE_PLAY || mode == MODE_RECORD) {
						Track selectedTrack = songlist.getSelectedValue();
						Playlist playlist = PersistenceHandler.loadPlaylist();
						// TODO: wrong location to do this
						playlist.addTrack(selectedTrack);
						PlayerController.getInstance().setTrack(selectedTrack);
						GamePanel gameFrame = new GamePanel();
						getNavigationController().pushPanel(gameFrame);
					} else if (mode == MODE_HIGHSCORE) {
						HighscorePanel highscorePanel = new HighscorePanel(
								songlist.getSelectedValue());
						getNavigationController().pushPanel(highscorePanel);
					}
				}
			}
		});

		mainMenuButton = new MenuButton(
				KeyboardHeroConstants.getString("back_to_menu"), new Color(
						KeyboardHeroConstants.FONT_COLOR_SECONDARY));
		mainMenuButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				getNavigationController().popPanel();
			}
		});
		add(mainMenuButton, "growx, height 60!");
	}

	/**
	 * Inits the playlist.
	 */
	public void initPlaylist() {
		songlist = new MenuSongList<Track>(mode == MODE_RECORD);
		songlist.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

		playlist = PersistenceHandler.loadPlaylist();
		if (mode == MODE_RECORD) {
			songlist.setModel(playlist);
			transferHandler = new PlaylistTransferHandler(playlist);
			songlist.setDropMode(DropMode.ON);
			songlist.setTransferHandler(transferHandler);
		} else {
			songlist.setModel(playlist.getPlaylistWithPlayableTracks());
		}

		scrollPane = new JScrollPane(songlist);
		scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		add(scrollPane, "wrap, growx, pushy, growy");
	}

	/**
	 * Inits the search field.
	 */
	private void initSearchField() {
		searchField = new JTextField();
		searchField.setForeground(Color.white);
		searchField.setCaretColor(Color.white);
		searchField.setFont(new Font("SansSerif", Font.BOLD, 14));
		Color searchFieldColor = new Color(
				KeyboardHeroConstants.FONT_COLOR_PRIMARY);
		searchField.setBackground(searchFieldColor);
		searchField.setBorder(BorderFactory.createCompoundBorder(
				new LineBorder(searchFieldColor.darker(), 2), new EmptyBorder(
						8, 8, 8, 8)));

		// Placeholder
		textPrompt = new TextPrompt(
				KeyboardHeroConstants.getString("search_sound_cloud"),
				searchField, Show.FOCUS_LOST);
		textPrompt.setBorder(null);

		searchField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				final String search = searchField.getText();
				searchField.setText("");
				songlist.requestFocus();
				textPrompt.setText(KeyboardHeroConstants.getString("loading")
						+ " '" + search + "'");
				textPrompt.setHorizontalAlignment(JLabel.CENTER);
				new Thread(new Runnable() {

					@Override
					public void run() {
						final Playlist list = PlayerController.getInstance()
								.getSoundCloud().search(search);
						SwingUtilities.invokeLater(new Runnable() {

							@Override
							public void run() {
								songlist.setModel(list);
								textPrompt.setText(KeyboardHeroConstants
										.getString("search_sound_cloud"));
								textPrompt.setHorizontalAlignment(JLabel.LEFT);
								searchField.requestFocus();
							}
						});
					}
				}).start();
			}
		});

		add(searchField, "wrap, grow");
	}

	/* (non-Javadoc)
	 * @see view.GHPanel#didPressBack(java.awt.event.KeyEvent)
	 */
	@Override
	public void didPressBack(KeyEvent e) {
		getNavigationController().popToRootPanel();
	}
}