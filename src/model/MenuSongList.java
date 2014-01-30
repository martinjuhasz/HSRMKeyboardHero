/**
 * 
 * 
 * @author Simon Seyer
 * @author Martin Juhasz
 * @author Julia Kraft
 * @author Moritz Moeller
 * 
 */
package model;

import helper.KeyboardHeroConstants;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;

public class MenuSongList<E> extends JList<E> {

	private ImageIcon soundCloudIcon;
	private ImageIcon noteIcon;

	/**
	 * Instantiates a new menu song list.
	 * 
	 * @param dataModel the data model
	 * @param showIcons the show icons
	 */
	public MenuSongList(ListModel<E> dataModel, boolean showIcons) {
		super(dataModel);
		init(showIcons);
	}

	/**
	 * Instantiates a new menu song list.
	 * 
	 * @param showIcons the show icons
	 */
	public MenuSongList(boolean showIcons) {
		init(showIcons);
	}

	/**
	 * Inits the.
	 * 
	 * @param showIcons the show icons
	 */
	private void init(final boolean showIcons) {
		if (showIcons) {
			try {
				soundCloudIcon = new ImageIcon(ImageIO.read(getClass()
						.getResourceAsStream("/soundcloud.png")));
			} catch (IOException e) {
			}
			try {
				noteIcon = new ImageIcon(ImageIO.read(getClass()
						.getResourceAsStream("/note.png")));
			} catch (IOException e) {

			}
		}

		this.setFixedCellHeight(60);

		DefaultListCellRenderer renderer = new DefaultListCellRenderer() {

			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {

				Component c = super.getListCellRendererComponent(list, value,
						index, isSelected, cellHasFocus);

				if (showIcons && value instanceof Track && c instanceof JLabel) {
					Track track = (Track) value;
					JLabel label = (JLabel) c;
					label.setHorizontalTextPosition(JLabel.LEFT);
					label.setIconTextGap(15);
					if (track.getStrokeSet() != null) {
						label.setIcon(noteIcon);
					} else if (track.getMp3() instanceof MP3PlayerRemoteTrack) {
						label.setIcon(soundCloudIcon);
					}
				}
				c.setBackground(new Color(
						KeyboardHeroConstants.FONT_COLOR_PRIMARY));
				c.setForeground(Color.WHITE);
				c.setFont(new Font("SansSerif", Font.BOLD, 14));

				setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));

				return c;
			}

			@Override
			public int getHorizontalAlignment() {
				return CENTER;
			}

		};

		setCellRenderer(renderer);
	}

}
