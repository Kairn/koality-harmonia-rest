package io.esoma.khr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * The entity that models an album track.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "TRACK")
public class Track implements Serializable {

	private static final long serialVersionUID = 1L;

	private int trackId;
	private String trackName;
	private String composer;
	private int trackLength;
	private byte[] audio;
	private String audioType;
	private String isDemo;

	private Album album;
	private String audioDataUrl;

	public Track() {
		super();
	}

	public Track(int trackId) {
		super();
		this.trackId = trackId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "trackIdGen")
	@SequenceGenerator(name = "trackIdGen", sequenceName = "SEQ_TRACK_ID", allocationSize = 1)
	@Column(name = "TRACK_ID")
	public int getTrackId() {
		return trackId;
	}

	public void setTrackId(int trackId) {
		this.trackId = trackId;
	}

	@Column(name = "TRACK_NAME")
	public String getTrackName() {
		return trackName;
	}

	public void setTrackName(String trackName) {
		this.trackName = trackName;
	}

	@Column(name = "COMPOSER")
	public String getComposer() {
		return composer;
	}

	public void setComposer(String composer) {
		this.composer = composer;
	}

	@Column(name = "TRACK_LENGTH")
	public int getTrackLength() {
		return trackLength;
	}

	public void setTrackLength(int trackLength) {
		this.trackLength = trackLength;
	}

	@Column(name = "AUDIO")
	public byte[] getAudio() {
		return audio;
	}

	public void setAudio(byte[] audio) {
		this.audio = audio;
	}

	@Column(name = "AUDIO_TYPE")
	public String getAudioType() {
		return audioType;
	}

	public void setAudioType(String audioType) {
		this.audioType = audioType;
	}

	@Column(name = "IS_DEMO")
	public String getIsDemo() {
		return isDemo;
	}

	public void setIsDemo(String isDemo) {
		this.isDemo = isDemo;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ALBUM_ID")
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	@Transient
	public String getAudioDataUrl() {
		return audioDataUrl;
	}

	public void setAudioDataUrl(String audioDataUrl) {
		this.audioDataUrl = audioDataUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + trackId;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Track other = (Track) obj;
		if (trackId != other.trackId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Track [trackId=" + trackId + ", trackName=" + trackName + ", composer=" + composer + ", trackLength="
				+ trackLength + "]";
	}

}
