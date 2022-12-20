package com.study.springbatch.batch.chunk.processor;

import com.study.springbatch.batch.domain.Product;
import com.study.springbatch.batch.domain.ProductVO;
import org.modelmapper.ModelMapper;
import org.springframework.batch.item.ItemProcessor;

public class FIleItemProcessor implements ItemProcessor<ProductVO, Product> {

	@Override
	public Product process(ProductVO productVO) throws Exception {

		// 걱체 변환을 도와줌
		ModelMapper modelMapper = new ModelMapper();
		return modelMapper.map(productVO, Product.class); // productVO -> Product로 변환
	}
}
