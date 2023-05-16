package com.wolmerica.service.speciesbreed;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

/**
 * @author  Richard Wolschlager
 * @date    July 31, 2010
 */
public interface SpeciesBreedService {

  public String getSpeciesName(Connection conn,
                               Integer speciesKey)
  throws Exception, SQLException;

  public Integer getSpeciesKeyByName(Connection conn,
                                    String speciesName)
  throws Exception, SQLException;

  public String getBreedName(Connection conn,
                             Integer breedKey)
  throws Exception, SQLException;

  public Integer getBreedKeyByName(Connection conn,
                                    String breedName)
  throws Exception, SQLException;

  public HashMap GetSexAndNeuterIdByCode(Connection conn,
                                         String sexCode)
  throws Exception, SQLException;


}
