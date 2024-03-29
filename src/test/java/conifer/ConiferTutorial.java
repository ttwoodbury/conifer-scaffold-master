package conifer;

import java.util.Random;

import conifer.ctmc.CTMC;
import conifer.factors.TreeLikelihood;
import conifer.moves.SingleBranchScaling;
import conifer.moves.SingleNNI;

import bayonet.distributions.Exponential;
import bayonet.distributions.Uniform;
import bayonet.distributions.Exponential.MeanParameterization;
import bayonet.distributions.Uniform.MinMaxParameterization;
import blang.annotations.DefineFactor;
import blang.annotations.Samplers;
import tutorialj.Tutorial;



public class ConiferTutorial
{
  /**
   * # conifer
   * 
   * Installing from source
   * ----------------------
   * 
   * Requires: gradle, git, eclipse
   * 
   * - Clone the repository
   * - Type ``gradle eclipse`` from the root of the repository
   * - From eclipse:
   *   - ``Import`` in ``File`` menu
   *   - ``Import existing projects into workspace``
   *   - Select the root of the newly created repo
   *   - Deselect ``Copy projects into workspace`` to avoid having duplicates
   * 
   */
  @Tutorial(startTutorial = "README.md", showSource = false)
  public void usage() {}
  
  
  /**
   * Bayesian inference using Blang
   * -----------------------------
   * 
   * You will first learn to use ``Blang``, a new package where you can
   * write Bayesian models in a way reminiscent to JAGS, BUGS, etc. The 
   * main advantage of Blang is that you can easily add other types of 
   * random variables. This will be important in this exercise as we will
   * need tree-valued random variables.
   * 
   * ### Small example: predicting the number of future members of the human species
   * 
   * Before creating new types of random variables, let us start with an example using
   * familiar, real-valued random variable to familiarize ourself with the main Blang
   * concepts. To do so, we will follow the lines of the most basic version of the 
   * [Doomsday argument](http://en.wikipedia.org/wiki/Doomsday_argument).
   * 
   * #### Overview
   * 
   * Suppose you want to predict the number N of future members of the human species. 
   * 
   * From archaeological records, we know there are about 0.06 trillion humans have been 
   * born so far. Call this our observation Y. Let us assume that Y is uniformly 
   * distributed between 0 and N.
   * 
   * So if we put a prior on N, we can compute a posterior on the quantity of interest,
   * N - Y. Let us be optimistic, and let us put an exponential prior on N with a mean of
   * 10.0 trillion humans.
   * 
   * #### Basic concepts in Blang
   * 
   * Let us look now at how we can write this model in Blang.
   */
  @Tutorial(showSource = false, nextStep = BlangSmallExample.class)
  public void usingBlang() {}
  
  
  /**
   * ### Extending Blang
   * 
   * Let us see now Blang can be extended beyond its (currently very minimal) set of built-in 
   * factor and variables. At the same time, some more advanced programming concepts will be 
   * introduced (generics, test units, annotations). 
   * 
   * #### Creating a new factor
   * 
   * In this part, you will create a new type of factor to model Gamma densities.
   * 
   * Here are the main steps:
   * 
   * 1. Create a class ``Gamma``. Make this class implement ``Factor``.
   * 2. Create three fields of type ``RealVariable``, and annotated with 
   *    ``@FactorArgument``: one for the realization, one for the shape, one for the rate.
   *    Note that these should also be ``public`` and ``final``. The keyword ``public`` simply 
   *    means they are accessible from functions outside of the current one. 
   *    The keyword ``final`` means that the value of the field cannot be changed.
   * 3. Implement ``logDensity()``. You can the existing implementation provided by 
   *    the math-commons project (see how I did it in the ``Exponential`` class).
   * 4. Create a constructor.
   * 
   * That's it! We now have a working factor.
   * 
   * Before we move on, just a quick not on why we made the fields ``final``. Recall that almost all 
   * variables in java are in fact disguised pointers to memory addresses in the heap (the only 
   * exceptions are so-called primitives, ``int``, ``double``, ``char``, etc). This means that
   * the arguments of factor graphs will always point to the same memory location in the heap.
   * This ensures that if two different factors share a variable at the beginning, they will always 
   * share it throughout sampling. Note that even if the *reference* is final, we can still modify
   * what is the *contents* of this memory location. Under the hood, this is what the MCMC sampler
   * does (for example for real variables, via ``variable.setValue()``.
   * 
   * #### Improving the factor via object and generic programming
   * 
   * We will now do a few things to improve our implementation.
   * 
   * **Static methods:** It is often convenient to access the logic inside a method (function associated
   * with an object) without always 
   * having to instantiate an object (new ClassName().function()). As a consequence, when 
   * possible it is good to pull the 
   * contents of method into a ``static`` function. See ``logDensity(double point, double rate)``
   * in ``Exponential.java``.
   * This way, it is possible to get the density using ``Exponential.logDensity(x,r)``, or even 
   * shorter, by adding ``import static bayonet.distributions.Exponential.*`` at the top of the 
   * file and then accessing the function with just ``logDensity(x,r)``.
   * 
   * **Flexible parameterization:** Distribution families are often parameterized using more 
   * than one type of parameterization.
   * For example, we would like the same Gamma class to support both shape-scale and shape-rate 
   * parameterizations. Here are the detailed steps of how this can be done:
   * 
   * 1. Create an interface called ``Parameters``. In cases like here where an interface or class A is used mostly 
   *    in the context of another one B, it is a good idea to write A inside the file B.java as well.
   *    To do this, simply use ``public static interface B {}`` (``static`` here means that B does not
   *    have access to the fields in A; this is the best choice most of the time).
   * 2. Create two classes ``ShapeScaleParameterization`` and ``ShapeRateParameterization`` 
   *    using the same strategy, one where you copy the shape and rate from earlier (keeping
   *    their annotations), and the second where you create two new fields with again the appropriate 
   *    annotations. Make all the fields final and create constructors. Also, make the two class implement Parameters.
   * 3. Go back to the interface you have created, declare the signature of two methods, one that returns a rate,
   *    ``public double getRate();``
   *    one that return a shape.
   * 4. Eclipse will now underline in red the two classes you have created. This is because they do not 
   *    respect the contract set by their interfaces. Clicking on the corresponding red icon
   *    in the margin, and selecting ``Add unimplemented methods`` will create stubs for the missing methods.
   *    Fill these methods: in one class, this will just return the identity, in the other, it does some simple
   *    transformations.
   * 5. Finally, in the Gamma class, replace the shape and rate fields by an instance of Parameters, called ``parameters``. 
   *    Replace occurrences of ``rate.getValue()`` by ``parameters.getRate()``, and similarly for the shape. 
   *    Update the constructors.
   * 6. Annotate the new field parameters with ``@FactorComponent``. This means that while parameters is not
   *    by itself a variable connected to this factor, it does contain variables connected to this factor 
   *    (those in the implementation annotated by ``@FactorArgument``. Note that this can be recursive (a 
   *    factor component can contain further factor components, etc).
   * 
   * **Generics:** The class as-is is full functional, but using it in method definitions is a bit cumbersome:
   * for example if we wanted to put an exponential prior on the rate of a gamma, we would need to write something 
   * like:
   * 
   * ```java
   * @DefineFactor 
   * Gamma gamma = new Gamma(x);
   * 
   * @DefineFactor
   * Exponential exponential = Exponential.on(((RateParameterization) gamma.parameters).rate);
   * ```
   * 
   * This is because in its current form, the compiler only knows that parameters is of type Parameter,
   * it does not known the precise type (``ShapeScaleParameterization`` or ``ShapeRateParameterization``).
   * 
   * To address this, we will use generics, which give us a way to declare which flavor of Gamma we use right when
   * we declare it. The end result will be being able to write:
   * 
   * ```java
   * @DefineFactor 
   * Gamma<ShapeRateParameterization> gamma = new Gamma<ShapeRateParameterization>(x);
   * 
   * @DefineFactor
   * Exponential exponential = Exponential.on(gamma.parameters.rate);
   * ```
   * 
   * To do this, proceed as follows:
   * 1. Declare a generic type in the class declaration: ``public class Gamma<P extends Parameters>``. Here P is what is 
   *    called a *generic*. It is like a variable, but instead of holding a value, it holds a type (class or interface name).
   *    The part ``extends Parameters`` is called a type bound: it means that P is has to implement Parameters.
   * 2. Change ``public final Parameters parameters;`` to ``public final P parameters;``.
   * 
   * **Method chaining:** This is a technique that again makes it just cleaner to  define models. This allows writing 
   * ``Gamma.on(x).withShapeAndRate(y, z)``. See examples at the 
   * bottom of ``Exponential.java``.
   * 
   */
  @Tutorial(showSource = false, nextStep = TestBlangSmallExample.class)
  public void extendingBlang() {}
  
  /**
   * Bayesian Phylogenetic inference
   * -------------------------------
   * 
   * We will now apply this framework to a more complex problem, Bayesian phylogenetic
   * inference. We start with the core of the likelihood calculation, the matrix exponential.
   * 
   * #### Implementing the matrix exponential
   */
  @Tutorial(showSource = false, nextStep = CTMC.class)
  public void phyloApplicationPart1() {}
  
  /**
   * #### Computing the likelihood on a tree
   */
  @Tutorial(showSource = false, nextStep = TreeLikelihood.class)
  public void phyloApplicationPart2() {}
  
  /**
   * #### Sampling the tree
   * 
   * We now have a working likelihood factor, but note the tree random variable it is connected
   * to is not of type supported by default in Blang. This brings us to the next topic, how to 
   * create samplers for new datatypes. Notice that at the top of the class declaration of UnrootedTree,
   * there is an annotation ``@Samplers({SingleNNI.class, SingleBranchScaling.class})``, which 
   * specifies which samplers to use for this type of object. 
   * 
   * Let us look at one of them, SingleNNI.
   */
  @Tutorial(showSource = false, nextStep = SingleNNI.class)
  public void phyloApplicationPart3() {}
  
  /**
   * #### Running the phylogenetic sampler
   * 
   * Finally, you can run the phylogenetic sampler. More detail will be added shortly.
   */
  @Tutorial(showSource = false, nextStep = SimplePhyloModel.class)
  public void runningPhylo() {}
}
