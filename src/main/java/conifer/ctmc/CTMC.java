package conifer.ctmc;

import tutorialj.Tutorial;


public class CTMC
{
  private final RateMatrix rateMatrix;
  
  public CTMC(RateMatrix rateMatrix)
  {
    this.rateMatrix = rateMatrix;
  }
  
  /**
   * 
   * Fill in ``marginalTransitionProbability()``.
   * 
   * Use the diagonalization method covered in class, using 
   * the eigen-decomposition functionalities provided by EJML.
   */
  @Tutorial(showSource = false, showLink = true)
  public double [][] marginalTransitionProbability(double branchLength)
  {
    throw new RuntimeException();
      
  }

  /**
   * Fill in ``stationaryDistribution()``.
   * 
   * Show how you can reduce the problem of finding the stationary distribution
   * of a CTMC to a eigenvalue problem. Again, solve this problem using
   * EJML.
   */
  @Tutorial(showSource = false, showLink = true)
  public double [] stationaryDistribution()
  {
    throw new RuntimeException();
      
  }
  

}
