package curso.springboot.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;

@Repository
@Transactional
public interface TelefoneRepository extends JpaRepository<Telefone, Long>, JpaSpecificationExecutor<Telefone>{

	@Query("select t from Telefone t where UPPER(t.pessoa.nome) like UPPER(concat('%', ?1,'%'))")
	List<Telefone> findTelefoneByName(String nomePesquisa);

	@Query("select t from Telefone t where t.pessoa.id = ?1")	
    public List<Telefone> getTelefones(Long pessoaid);

	@Query("select t from Telefone t order by t.pessoa.nome")
	Iterable<Telefone> findAllOrder();
	
	default Page<Telefone> findTelefoneByNamePage(String nome, Pageable pageable) {
		Telefone telefone = new Telefone();
		Pessoa pessoa = new Pessoa();
		pessoa.setNome(nome);
		telefone.setPessoa(pessoa);
		ExampleMatcher exampleMatcher = ExampleMatcher.matchingAny().withMatcher("pessoa.nome", ExampleMatcher.GenericPropertyMatchers.contains().ignoreCase());
		Example<Telefone> example = Example.of(telefone, exampleMatcher);
		Page<Telefone> telefones = findAll(example, pageable);
		return telefones;
	}
}
