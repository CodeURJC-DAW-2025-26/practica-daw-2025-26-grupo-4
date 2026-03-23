package es.urjc.daw04.controllers.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.urjc.daw04.model.Category;
import es.urjc.daw04.model.Product;
import es.urjc.daw04.model.dto.CategoryDTO;
import es.urjc.daw04.model.dto.HomeProductsResponseDTO;
import es.urjc.daw04.model.dto.HomeResponseDTO;
import es.urjc.daw04.model.dto.ProductDTO;
import es.urjc.daw04.model.mapper.CategoryMapper;
import es.urjc.daw04.model.mapper.ProductMapper;
import es.urjc.daw04.service.CategoryService;
import es.urjc.daw04.service.ProductService;

@RestController
@RequestMapping({ "/api/v1/home"})
public class HomeRestController {

	private static final int HOME_PAGE_SIZE = 6;

	@Autowired
	private ProductService productService;

	@Autowired
	private CategoryService categoryService;

	@Autowired
	private ProductMapper productMapper;

	@Autowired
	private CategoryMapper categoryMapper;

	@GetMapping({ "", "/" })
	public HomeResponseDTO home(
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String q) {

		List<Category> categories = categoryService.findAll();
		Long selectedCategoryId = categoryId;

		if (selectedCategoryId == null && !categories.isEmpty()) {
			selectedCategoryId = categories.get(0).getId();
		}

		String searchQuery = q == null ? "" : q.trim();

		Page<Product> firstPage;
		if (selectedCategoryId == null) {
			firstPage = Page.empty();
		} else if (!searchQuery.isEmpty()) {
			firstPage = productService.searchByCategoryIdPaged(selectedCategoryId, searchQuery, 0, HOME_PAGE_SIZE);
		} else {
			firstPage = productService.findByCategoryIdPaged(selectedCategoryId, 0, HOME_PAGE_SIZE);
		}

		String selectedCategoryName = "Plantas";
		for (Category category : categories) {
			if (selectedCategoryId != null && selectedCategoryId.equals(category.getId())) {
				selectedCategoryName = category.getName();
				break;
			}
		}

		List<CategoryDTO> categoriesDTO = categoryMapper.toDTOs(categories);
		List<ProductDTO> productsDTO = productMapper.toDTOs(firstPage.getContent());

		return new HomeResponseDTO(
				categoriesDTO,
				productsDTO,
				selectedCategoryId,
				selectedCategoryName,
				searchQuery,
				!firstPage.isEmpty() && firstPage.hasNext());
	}

	@GetMapping("/products")
	public HomeProductsResponseDTO products(
			@RequestParam(required = false) Long categoryId,
			@RequestParam(required = false) String q,
			@RequestParam(defaultValue = "1") int page) {

		String searchQuery = q == null ? "" : q.trim();
		Page<Product> productPage;

		if (categoryId == null) {
			productPage = Page.empty();
		} else if (!searchQuery.isEmpty()) {
			productPage = productService.searchByCategoryIdPaged(categoryId, searchQuery, page, HOME_PAGE_SIZE);
		} else {
			productPage = productService.findByCategoryIdPaged(categoryId, page, HOME_PAGE_SIZE);
		}

		return new HomeProductsResponseDTO(
				productMapper.toDTOs(productPage.getContent()),
				!productPage.isEmpty() && productPage.hasNext());
	}
}
