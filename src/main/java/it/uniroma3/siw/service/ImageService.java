package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ImageRepository;


@Service
public class ImageService {
	
	@Autowired
	private ImageRepository imageRepository;

	@Transactional
	public byte[] getImage(Long id) {
		Image image = imageRepository.findById(id).orElseThrow();
		return image.getImageData();
	}
}