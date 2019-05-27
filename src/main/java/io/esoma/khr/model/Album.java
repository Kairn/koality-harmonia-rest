package io.esoma.khr.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * 
 * The entity that models an album.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "ALBUM")
public class Album implements Serializable {

	public static final Integer[] BASIC_COLLECTION = new Integer[] { 1, 2, 4, 5, 7, 9, 17, 24, 28 };

	private static final long serialVersionUID = 1L;

	private int albumId;
	private String albumName;
	private String artist;
	private byte[] artwork;
	private String artworkType;
	private int etaPrice;
	private String isPromoted;
	private String isPublished;

	private Genre genre;
	private Koalibee koalibee;
	private List<Track> trackList;
	private List<Review> reviewList;
	private String artworkDataUrl;

	public Album() {
		super();
	}

	public Album(int albumId) {
		super();
		this.albumId = albumId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "albumIdGen")
	@SequenceGenerator(name = "albumIdGen", sequenceName = "SEQ_ALBUM_ID", allocationSize = 1)
	@Column(name = "ALBUM_ID")
	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	@Column(name = "ALBUM_NAME")
	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	@Column(name = "ARTIST")
	public String getArtist() {
		return artist;
	}

	public void setArtist(String artist) {
		this.artist = artist;
	}

	@Lob
	@Column(name = "ARTWORK", columnDefinition = "BLOB")
	@Basic(fetch = FetchType.LAZY)
	public byte[] getArtwork() {
		return artwork;
	}

	public void setArtwork(byte[] artwork) {
		this.artwork = artwork;
	}

	@Column(name = "ARTWORK_TYPE")
	public String getArtworkType() {
		return artworkType;
	}

	public void setArtworkType(String artworkType) {
		this.artworkType = artworkType;
	}

	@Column(name = "ETA_PRICE")
	public int getEtaPrice() {
		return etaPrice;
	}

	public void setEtaPrice(int etaPrice) {
		this.etaPrice = etaPrice;
	}

	@Column(name = "IS_PROMOTED")
	public String getIsPromoted() {
		return isPromoted;
	}

	public void setIsPromoted(String isPromoted) {
		this.isPromoted = isPromoted;
	}

	@Column(name = "IS_PUBLISHED")
	public String getIsPublished() {
		return isPublished;
	}

	public void setIsPublished(String isPublished) {
		this.isPublished = isPublished;
	}

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "GENRE_ID")
	public Genre getGenre() {
		return genre;
	}

	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KOALIBEE_ID")
	public Koalibee getKoalibee() {
		return koalibee;
	}

	public void setKoalibee(Koalibee koalibee) {
		this.koalibee = koalibee;
	}

	@OneToMany(mappedBy = "album")
	public List<Track> getTrackList() {
		return trackList;
	}

	public void setTrackList(List<Track> trackList) {
		this.trackList = trackList;
	}

	@OneToMany(mappedBy = "album")
	public List<Review> getReviewList() {
		return reviewList;
	}

	public void setReviewList(List<Review> reviewList) {
		this.reviewList = reviewList;
	}

	@Transient
	public String getArtworkDataUrl() {
		return artworkDataUrl;
	}

	public void setArtworkDataUrl(String artworkDataUrl) {
		this.artworkDataUrl = artworkDataUrl;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + albumId;
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
		Album other = (Album) obj;
		if (albumId != other.albumId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Album [albumId=" + albumId + ", albumName=" + albumName + ", artist=" + artist + "]";
	}

}
