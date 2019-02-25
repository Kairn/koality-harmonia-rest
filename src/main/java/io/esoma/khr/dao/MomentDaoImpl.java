package io.esoma.khr.dao;

import java.time.LocalDate;
import java.util.List;

import io.esoma.khr.model.Moment;

/**
 * 
 * The basic implementation of MomentDao interface using Hibernate 5.
 * 
 * @author Eddy Soma
 *
 */
public class MomentDaoImpl implements MomentDao {

	@Override
	public Moment getMomentById(int momentId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Moment getMomentByKoalibeeAndDate(int koalibeeId, LocalDate postDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int addMoment(Moment moment) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Moment> getAllMoments() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Moment> getAllMomentsByDate(LocalDate postDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteMoment(int momentId) {
		// TODO Auto-generated method stub
		return false;
	}

}
