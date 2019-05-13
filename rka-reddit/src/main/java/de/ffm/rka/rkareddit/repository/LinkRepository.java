package de.ffm.rka.rkareddit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import de.ffm.rka.rkareddit.domain.Link;

public interface LinkRepository extends JpaRepository<Link, Long> {

}
