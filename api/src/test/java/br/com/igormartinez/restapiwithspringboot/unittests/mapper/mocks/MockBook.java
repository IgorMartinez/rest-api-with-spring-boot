package br.com.igormartinez.restapiwithspringboot.unittests.mapper.mocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.com.igormartinez.restapiwithspringboot.data.vo.v1.BookVO;
import br.com.igormartinez.restapiwithspringboot.model.Book;

public class MockBook {

    public Book mockEntity() {
        return mockEntity(0);
    }

    public BookVO mockVO() {
        return mockVO(0);
    }

    public List<Book> mockEntityList() {
        List<Book> list = new ArrayList<>();
        for (int i=0; i < 14; i++) {
            list.add(mockEntity(i));
        }
        return list;
    }

    public List<BookVO> mockVOList() {
        List<BookVO> list = new ArrayList<>();
        for (int i=0; i < 14; i++) {
            list.add(mockVO(i));
        }
        return list;
    }

    public Book mockEntity(Integer number) {
        Book mock = new Book();
        mock.setId(number.longValue());
        mock.setAuthor("Author " + number);
        mock.setLaunchDate(new Date(number));
        mock.setTitle("Title " + number);
        mock.setPrice(number.doubleValue());
        return mock;
    }

    public BookVO mockVO(Integer number) {
        BookVO mock = new BookVO();
        mock.setId(number.longValue());
        mock.setAuthor("Author " + number);
        mock.setLaunchDate(new Date(number));
        mock.setTitle("Title " + number);
        mock.setPrice(number.doubleValue());
        return mock;
    }
}
