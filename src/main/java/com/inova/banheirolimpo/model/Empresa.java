/**
 * 
 */
package com.inova.banheirolimpo.model;

import javax.persistence.Entity;
import javax.persistence.ForeignKey;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author Markus Souza on 08/11/2017
 *
 */

@Entity
@Data
@EqualsAndHashCode(callSuper = false)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Empresa extends AbstractEmpresa {
	
	private static final long serialVersionUID = 1071168608696477420L;
	
	@ManyToOne
	@JoinColumn(name = "empresa_pai_id", foreignKey = @ForeignKey(name = "FK_EMPRESA_PAI"))
	public Empresa filial;

}
