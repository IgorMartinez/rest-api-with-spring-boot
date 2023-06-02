package br.com.igormartinez.restapiwithspringboot.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.BookVO;

public class BookEmbeddedVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("bookVOList")
    private List<BookVO> listBookVO;

    public BookEmbeddedVO() {}

    public List<BookVO> getListBookVO() {
        return listBookVO;
    }

    public void setListBookVO(List<BookVO> listBookVO) {
        this.listBookVO = listBookVO;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((listBookVO == null) ? 0 : listBookVO.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        BookEmbeddedVO other = (BookEmbeddedVO) obj;
        if (listBookVO == null) {
            if (other.listBookVO != null)
                return false;
        } else if (!listBookVO.equals(other.listBookVO))
            return false;
        return true;
    }

}
