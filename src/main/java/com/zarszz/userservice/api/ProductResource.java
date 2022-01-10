package com.zarszz.userservice.api;

import java.net.URI;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

import javax.validation.Valid;

import com.zarszz.userservice.domain.Product;
import com.zarszz.userservice.response.v1.exception.ErrorResponse;
import com.zarszz.userservice.service.ProductServiceImpl;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import com.zarszz.userservice.requests.v1.product.CreateProductDto;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProductResource {

    public static final String TRACE = "trace";

    @Value("${reflectoring.trace:false}")
    private boolean printStackTrace;

    @Autowired
    private final ProductServiceImpl productService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping("/products")
    ResponseEntity<List<Product>> getProducts() {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products").toUriString());
        return ResponseEntity.created(uri).body(this.productService.getProducts());
    }

    @PostMapping("/products")
    ResponseEntity<Product> create(@Valid @RequestBody CreateProductDto productDto) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/products").toUriString());
        Product product = convertToEntity(productDto);
        return ResponseEntity.created(uri).body(this.productService.save(product));
    }

    @GetMapping("/products/{id}")
    ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productService.get(id));
    }

    @ExceptionHandler(NoSuchElementException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleItemNotFoundException(
            NoSuchElementException exception,
            WebRequest request) {
        return buildErrorResponse(exception, HttpStatus.NOT_FOUND, request);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Validation error. Check 'errors' field for details.");

        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            errorResponse.addValidationError(fieldError.getField(), fieldError.getDefaultMessage());
        }
        return ResponseEntity.unprocessableEntity().body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleAllUncaughtException(
            Exception exception,
            WebRequest request) {
        return buildErrorResponse(exception, "Unknown error occurred", HttpStatus.INTERNAL_SERVER_ERROR, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception exception,
            HttpStatus httpStatus,
            WebRequest request) {
        return buildErrorResponse(exception, exception.getMessage(), httpStatus, request);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(
            Exception exception,
            String message,
            HttpStatus httpStatus,
            WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(httpStatus.value(), exception.getMessage());
        if (printStackTrace && isTraceOn(request)) {
            errorResponse.setStackTrace(ExceptionUtils.getStackTrace(exception));
        }
        return ResponseEntity.status(httpStatus).body(errorResponse);
    }

    private boolean isTraceOn(WebRequest request) {
        String[] value = request.getParameterValues(TRACE);
        return Objects.nonNull(value)
                && value.length > 0
                && value[0].contentEquals("true");
    }

    private Product convertToEntity(CreateProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }
}
