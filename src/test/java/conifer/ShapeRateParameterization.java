package conifer;


import blang.annotations.FactorArgument;
import blang.variables.RealVariable;

public class ShapeRateParameterization implements Parameters {
	
	@FactorArgument
	public final RealVariable realization;
	
	@FactorArgument
	public final RealVariable rate;
	
	@FactorArgument
	public final RealVariable shape;
	
	
	
	public ShapeRateParameterization(RealVariable rate1, RealVariable shape1, RealVariable realization1) {
		realization = realization1;
		rate = rate1;
		shape = shape1;
	}

	@Override
	public double getRate() {
		return rate.getValue();
	}

	@Override
	public double getShape() {
		return shape.getValue();
	}
	

}
