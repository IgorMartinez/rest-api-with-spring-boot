package br.com.igormartinez.restapiwithspringboot.integrationtests.vo.wrappers;

import java.io.Serializable;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import br.com.igormartinez.restapiwithspringboot.integrationtests.vo.PersonVO;

public class PersonEmbeddedVO implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("personVOList")
    private List<PersonVO> listPersonVO;

    public PersonEmbeddedVO() {}

    public List<PersonVO> getListPersonVO() {
        return listPersonVO;
    }

    public void setListPersonVO(List<PersonVO> listPersonVO) {
        this.listPersonVO = listPersonVO;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((listPersonVO == null) ? 0 : listPersonVO.hashCode());
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
        PersonEmbeddedVO other = (PersonEmbeddedVO) obj;
        if (listPersonVO == null) {
            if (other.listPersonVO != null)
                return false;
        } else if (!listPersonVO.equals(other.listPersonVO))
            return false;
        return true;
    }

}
