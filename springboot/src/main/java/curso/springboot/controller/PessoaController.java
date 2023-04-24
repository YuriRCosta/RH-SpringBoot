package curso.springboot.controller;

import java.util.List;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import curso.springboot.model.Pessoa;
import curso.springboot.model.Telefone;
import curso.springboot.repository.PessoaRepository;
import curso.springboot.repository.ProfissaoRepository;
import curso.springboot.repository.TelefoneRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Controller
public class PessoaController {

	@Autowired
	private TelefoneRepository telefoneRepository;
	
	@Autowired
	private PessoaRepository pessoaRepository;
	
	@Autowired
	private ReportUtil reportUtil;
	
	@Autowired
	private ProfissaoRepository profissaoRepository;
	
	@RequestMapping(method =RequestMethod.GET, value = "/cadastropessoa")
	public ModelAndView inicio() {
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@RequestMapping(method =RequestMethod.GET, value = "/inicio")
	public ModelAndView inicioPage() {
		ModelAndView modelAndView = new ModelAndView("inicio");
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.POST, value = "/salvarpessoa", consumes = {"multipart/form-data"})
	public ModelAndView salvar(Pessoa pessoa, final MultipartFile file) throws IOException {
		
		pessoa.setTelefones(telefoneRepository.getTelefones(pessoa.getId()));
		
		if((pessoa.getNome() == null || pessoa.getNome().isEmpty()) || (pessoa.getSobrenome() == null || pessoa.getSobrenome().isEmpty()))  {
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
			Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
			modelAndView.addObject("pessoas", pessoasIt);
			modelAndView.addObject("pessoaobj", new Pessoa());
			return modelAndView;
		}
		if(file.getSize() > 0) {
			pessoa.setCurriculo(file.getBytes());
			pessoa.setTipoFileCurriculo(file.getContentType());
			pessoa.setNomeFileCurriculo(file.getOriginalFilename());
		} else if(pessoa.getId() != null && pessoa.getId() > 0){
			Pessoa pessoaTemp = pessoaRepository.findById(pessoa.getId()).get();
			pessoa.setCurriculo(pessoaTemp.getCurriculo());
			pessoa.setTipoFileCurriculo(pessoaTemp.getTipoFileCurriculo());
			pessoa.setNomeFileCurriculo(pessoaTemp.getNomeFileCurriculo());
		}
		pessoaRepository.save(pessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		Iterable<Pessoa> pessoasIt = pessoaRepository.findAll();
		modelAndView.addObject("pessoas", pessoasIt);
		modelAndView.addObject("pessoaobj", new Pessoa());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/pesquisapessoa")
	public ModelAndView pessoas() {
		ModelAndView modelAndView = new ModelAndView("pesquisa/pesquisapessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		return modelAndView;
	}
	
	@GetMapping("/editarpessoa/{idpessoa}")
	public ModelAndView editar(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastropessoa");
		modelAndView.addObject("pessoaobj", pessoa.get());
		modelAndView.addObject("profissoes", profissaoRepository.findAll());
		return modelAndView;
	}
	
	@GetMapping("/excluirpessoa/{idpessoa}")
	public ModelAndView excluir(@PathVariable("idpessoa") Long idpessoa) {
		pessoaRepository.deleteById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("pesquisa/pesquisapessoa");
		modelAndView.addObject("pessoas", pessoaRepository.findAll(PageRequest.of(0, 5, Sort.by("nome"))));
		return modelAndView;
	}
	
	@PostMapping("/pesquisarpessoa")
	public ModelAndView searchByName(@RequestParam("nomePesquisa") String nomePesquisa, @PageableDefault(size = 5, sort = {"nome"}) Pageable pageable) {
		Page<Pessoa> pessoas = null;
		pessoas = pessoaRepository.findPessoaByNamePage(nomePesquisa, pageable);
		ModelAndView modelAndView = new ModelAndView("pesquisa/pesquisapessoa");
		modelAndView.addObject("pessoas", pessoas);
		modelAndView.addObject("nomePesquisa", nomePesquisa);
		return modelAndView;
	}
	
	@GetMapping("/pesquisarpessoa")
	public void imprimirPDF(@RequestParam("nomePesquisa") String nomePesquisa, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Pessoa> pessoas = new ArrayList<Pessoa>();
		if(nomePesquisa != null && !nomePesquisa.isEmpty()) {
			pessoas = pessoaRepository.findPessoaByName(nomePesquisa);
		} else {
			Iterable<Pessoa> iterator = pessoaRepository.findAllOrder();
			for (Pessoa pessoa : iterator) {
				pessoas.add(pessoa);
			}
		}
		byte[] pdf = reportUtil.gerarRelatorio(pessoas, "pessoa", request.getServletContext());
		response.setContentLength(pdf.length);
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		response.getOutputStream().write(pdf);
	}
	
	@GetMapping("/pesquisartelefone")
	public void imprimirPDFTelefone(@RequestParam("nomePesquisa") String nomePesquisa, HttpServletRequest request, HttpServletResponse response) throws Exception {
		List<Telefone> telefones = new ArrayList<Telefone>();
		if(nomePesquisa != null && !nomePesquisa.isEmpty()) {
			telefones = telefoneRepository.findTelefoneByName(nomePesquisa);
		} else {
			Iterable<Telefone> iterator = telefoneRepository.findAllOrder();
			for (Telefone telefone : iterator) {
				telefones.add(telefone);
			}
		}
		byte[] pdf = reportUtil.gerarRelatorio(telefones, "telefone", request.getServletContext());
		response.setContentLength(pdf.length);
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
		String headerValue = String.format("attachment; filename=\"%s\"", "relatorio.pdf");
		response.setHeader(headerKey, headerValue);
		response.getOutputStream().write(pdf);
	}
	
	@GetMapping("/telefonesPessoa/{idpessoa}")
	public ModelAndView telefones(@PathVariable("idpessoa") Long idpessoa) {
		Optional<Pessoa> pessoa = pessoaRepository.findById(idpessoa);
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefone");
		modelAndView.addObject("pessoaobj", pessoa.get());
		return modelAndView;
	}
	
	@PostMapping("/adicionarTelefone/{pessoaid}")
	public ModelAndView adicionarTelefone(Telefone telefone, @PathVariable("pessoaid") Long pessoaid) {
		
		if(telefone.getNumero() == null || telefone.getNumero().isEmpty())  {
			Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
			
			ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefone");
			modelAndView.addObject("pessoaobj", pessoa);
			return modelAndView;
		}
		
		Pessoa pessoa = pessoaRepository.findById(pessoaid).get();
		telefone.setPessoa(pessoa);
		telefoneRepository.save(telefone);
		
		ModelAndView modelAndView = new ModelAndView("cadastro/cadastrotelefone");
		modelAndView.addObject("pessoaobj", pessoa);
		return modelAndView;
	}
	
	@RequestMapping(method = RequestMethod.GET, value = "/pesquisatelefones")
	public ModelAndView telefonesLista() {
		ModelAndView modelAndView = new ModelAndView("pesquisa/pesquisatelefone");
		modelAndView.addObject("telefones", telefoneRepository.findAll(PageRequest.of(0, 5, Sort.by("pessoa.nome"))));
		return modelAndView;
	}
	
	@GetMapping("/excluirtelefone/{idtelefone}")
	public ModelAndView excluirTelefone(@PathVariable("idtelefone") Long idtelefone) {
		telefoneRepository.deleteById(idtelefone);
		ModelAndView modelAndView = new ModelAndView("pesquisa/pesquisatelefone");
		modelAndView.addObject("telefones", telefoneRepository.findAll(PageRequest.of(0, 5, Sort.by("pessoa.nome"))));
		return modelAndView;
	}
	
	@PostMapping("/pesquisartelefone")
	public ModelAndView searchByNameFone(@RequestParam("nomePesquisa") String nomePesquisa, @PageableDefault(size = 5, sort = {"pessoa.nome"}) Pageable pageable) {
		Page<Telefone> telefones = null;
		telefones = telefoneRepository.findTelefoneByNamePage(nomePesquisa, pageable);
		ModelAndView modelAndView = new ModelAndView("pesquisa/pesquisatelefone");
		modelAndView.addObject("telefones", telefones);
		modelAndView.addObject("nomePesquisa", nomePesquisa);
		return modelAndView;
	}
	
	@GetMapping("/baixarCurriculo/{idpessoa}")
	public void baixarCurriculo(@PathVariable("idpessoa") Long idpessoa, HttpServletResponse response) throws IOException {
		Pessoa pessoa = pessoaRepository.findById(idpessoa).get();
		if(pessoa.getCurriculo() != null) {
			response.setContentLength(pessoa.getCurriculo().length);
			response.setContentType(pessoa.getTipoFileCurriculo());
			String headerKey = "Content-Disposition";
			String headerValue = String.format("attachment; filename=\"%s\"", pessoa.getNomeFileCurriculo());
			response.setHeader(headerKey, headerValue);
			response.getOutputStream().write(pessoa.getCurriculo());
		}
	}
	
	@GetMapping("/pessoaspag")
	public ModelAndView carregaPessoasPorPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView model, @RequestParam("nomePesquisa") String nomePesquisa) {
		Page<Pessoa> pagePessoa = pessoaRepository.findPessoaByNamePage(nomePesquisa, pageable);
		model.addObject("pessoas", pagePessoa);
		model.addObject("nomePesquisa", nomePesquisa);
		model.setViewName("pesquisa/pesquisapessoa");
		return model;
	}
	
	@GetMapping("/telefonespag")
	public ModelAndView carregaTelefonesPorPaginacao(@PageableDefault(size = 5) Pageable pageable, ModelAndView model, @RequestParam("nomePesquisa") String nomePesquisa) {
		Page<Telefone> pageTelefone = telefoneRepository.findTelefoneByNamePage(nomePesquisa, pageable);
		model.addObject("telefones", pageTelefone);
		model.addObject("nomePesquisa", nomePesquisa);
		model.setViewName("pesquisa/pesquisatelefone");
		return model;
	}
}
