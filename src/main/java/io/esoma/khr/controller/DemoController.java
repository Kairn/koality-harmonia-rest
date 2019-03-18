package io.esoma.khr.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.esoma.khr.model.Album;
import io.esoma.khr.service.AlbumService;

/**
 * 
 * The controller class that is responsible for retrieving data and materials
 * that are publicly accessible to non-registered visitors.
 * 
 * @author Eddy Soma
 *
 */
@RestController(value = "demoController")
@RequestMapping(path = "/demo")
public class DemoController {

	// Albums IDs that are supposed to be demos.
	static int[] demoAlbums = new int[] { 1, 2, 3 };

	private AlbumService albumService;

	@Autowired
	@Qualifier(value = "albumService")
	public void setAlbumService(AlbumService albumService) {
		this.albumService = albumService;
	}

	/**
	 * 
	 * Responds to a HTTP request of retrieving all demo albums. No authentication
	 * token is needed.
	 * 
	 * @return the list containing the demo albums.
	 */
	@GetMapping(path = "/albums")
	public ResponseEntity<List<Album>> getDemoAlbums() {

		List<Album> albumList = new ArrayList<>();

		for (int i : demoAlbums) {
			Album album = this.albumService.getOne(i);
			if (album != null) {
				albumList.add(album);
			}
		}

		return ResponseEntity.ok(albumList);

	}

}
