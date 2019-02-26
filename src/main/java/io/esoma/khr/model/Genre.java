package io.esoma.khr.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * 
 * The entity that models a music genre.
 * 
 * @author Eddy Soma
 *
 */
@Entity
@Table(name = "GENRE")
public class Genre implements Serializable {

	private static final long serialVersionUID = 1L;

	private int genreId;
	private String genreName;

	public Genre() {
		super();
	}

	public Genre(int genreId) {
		super();
		this.genreId = genreId;
	}

	public Genre(int genreId, String genreName) {
		super();
		this.genreId = genreId;
		this.genreName = genreName;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "genreIdGen")
	@SequenceGenerator(name = "genreIdGen", sequenceName = "SEQ_GENRE_ID", allocationSize = 1)
	@Column(name = "GENRE_ID")
	public int getGenreId() {
		return genreId;
	}

	public void setGenreId(int genreId) {
		this.genreId = genreId;
	}

	@Column(name = "GENRE_NAME")
	public String getGenreName() {
		return genreName;
	}

	public void setGenreName(String genreName) {
		this.genreName = genreName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + genreId;
		result = prime * result + ((genreName == null) ? 0 : genreName.hashCode());
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
		Genre other = (Genre) obj;
		if (genreId != other.genreId) {
			return false;
		}
		if (genreName == null) {
			if (other.genreName != null) {
				return false;
			}
		} else if (!genreName.equals(other.genreName)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "Genre [genreId=" + genreId + ", genreName=" + genreName + "]";
	}

}
