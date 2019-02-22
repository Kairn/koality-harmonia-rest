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
 * The entity that models a review entry for an album.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "REVIEW")
public class Review implements Serializable {

	private static final long serialVersionUID = 1L;

	private int reviewId;
	private int rating;
	private String reviewComment;

	private Album album;
	private Koalibee koalibee;
	private String albumName;
	private String koalibeeName;

	public Review() {
		super();
	}

	public Review(int reviewId) {
		super();
		this.reviewId = reviewId;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "reviewIdGen")
	@SequenceGenerator(name = "reviewIdGen", sequenceName = "SEQ_REVIEW_ID", allocationSize = 1)
	@Column(name = "REVIEW_ID")
	public int getReviewId() {
		return reviewId;
	}

	public void setReviewId(int reviewId) {
		this.reviewId = reviewId;
	}

	@Column(name = "RATING")
	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	@Column(name = "REVIEW_COMMENT")
	public String getReviewComment() {
		return reviewComment;
	}

	public void setReviewComment(String reviewComment) {
		this.reviewComment = reviewComment;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALBUM_ID")
	public Album getAlbum() {
		return album;
	}

	public void setAlbum(Album album) {
		this.album = album;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "KOALIBEE_ID")
	public Koalibee getKoalibee() {
		return koalibee;
	}

	public void setKoalibee(Koalibee koalibee) {
		this.koalibee = koalibee;
	}

	@Transient
	public String getAlbumName() {
		return albumName;
	}

	public void setAlbumName(String albumName) {
		this.albumName = albumName;
	}

	@Transient
	public String getKoalibeeName() {
		return koalibeeName;
	}

	public void setKoalibeeName(String koalibeeName) {
		this.koalibeeName = koalibeeName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + reviewId;
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
		Review other = (Review) obj;
		if (reviewId != other.reviewId) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Review [reviewId=" + reviewId + ", rating=" + rating + "]";
	}

}
