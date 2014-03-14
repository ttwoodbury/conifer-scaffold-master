package conifer;

import org.apache.commons.math3.distribution.GammaDistribution;

import blang.annotations.FactorArgument;
import blang.annotations.FactorComponent;
import blang.factors.Factor;
import blang.variables.RealVariable;

public class Gamma<P extends Parameters> implements Factor{
	

	@FactorComponent
	public final RealVariable realization;
	
	@FactorComponent
	public final P rate;
	
	@FactorComponent
	public final P shape;
	
	
	public double logDensity() {
	GammaDistribution gamma = new GammaDistribution(shape.getShape(),1/rate.getRate());
	return Math.log(gamma.density(realization.getValue()));	
	}
	
	public Gamma(P rate1, P shape1, RealVariable realization1) {
		realization = realization1;
		rate = rate1;
		shape = shape1;
	}
	
	
}
