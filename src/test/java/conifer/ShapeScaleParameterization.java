package conifer;



import blang.annotations.FactorArgument;
import blang.variables.RealVariable;

public class ShapeScaleParameterization implements Parameters{
	
	@FactorArgument
	public final RealVariable realization;
	
	@FactorArgument
	public final RealVariable scale;
	
	@FactorArgument
	public final RealVariable shape;

	
	public ShapeScaleParameterization(RealVariable scale1, RealVariable shape1, RealVariable realization1) {
		realization = realization1;
		scale = scale1;
		shape = shape1;
	}

	@Override
	public double getRate() {
		return 1/scale.getValue();
	}

	@Override
	public double getShape() {
		return shape.getValue();
	}
	

}
