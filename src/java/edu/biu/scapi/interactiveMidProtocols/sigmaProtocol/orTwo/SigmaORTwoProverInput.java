/**
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
* Copyright (c) 2012 - SCAPI (http://crypto.biu.ac.il/scapi)
* This file is part of the SCAPI project.
* DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"),
* to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, 
* and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
* FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
* WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
* 
* We request that any publication and/or code referring to and/or based on SCAPI contain an appropriate citation to SCAPI, including a reference to
* http://crypto.biu.ac.il/SCAPI.
* 
* SCAPI uses Crypto++, Miracl, NTL and Bouncy Castle. Please see these projects for any further licensing issues.
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
*/
package edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.orTwo;

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaCommonInput;
import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProverInput;

/**
 * Concrete implementation of SigmaProtocol input, used by the SigmaProtocolORTwoProver.<p>
 * 
 * In SigmaProtocolORTwo protocol, the prover gets an input for the true statement (with witness), 
 * an input for the false statement (without witness) and a bit b, such that (xb,w) is in R.
 * 
 * @author Cryptography and Computer Security Research Group Department of Computer Science Bar-Ilan University (Moriya Farbstein)
 *
 */
public class SigmaORTwoProverInput implements SigmaProverInput{
	
	private SigmaProverInput proverInput;
	private SigmaCommonInput simulatorInput;
	private byte b;
	
	/**
	 * Sets the inputs for the underlying prover and simulator and a bit b, such that (xb,w) is in R.
	 * @param proverInput 
	 * @param simulatorInput
	 * @param b such that (xb,w) is in R.
	 */
	public SigmaORTwoProverInput(SigmaProverInput proverInput, SigmaCommonInput simulatorInput, byte b){
		this.proverInput = proverInput;
		this.simulatorInput = simulatorInput;
		this.b = b;
	}
	
	/**
	 * Returns the bit b such that (xb,w) is in R.
	 * @return the bit b such that (xb,w) is in R.
	 */
	public byte getB(){
		return b;
	}
	
	/**
	 * Returns the input for the underlying prover.
	 * @return the input for the underlying prover.
	 */
	public SigmaProverInput getProverInput(){
		return proverInput;
	}
	
	/**
	 * Returns the input for the underlying simulator.
	 * @return the input for the underlying simulator.
	 */
	public SigmaCommonInput getSimulatorInput(){
		return simulatorInput;
	}

	@Override
	public SigmaORTwoCommonInput getCommonParams() {
		/*
		 * 
		 * There are two options to implement this function:
		 * 1. Create a new instance of SigmaANDCommonInput every time the function is called.
		 * 2. Create the object in the construction time and return it every time this function is called.
		 * This class holds an array of SigmaProverInput, where each instance in the array holds 
		 * an instance of SigmaCommonParams inside it.
		 * In the second option above, this class will have in addition an array of SigmaCommonInput. 
		 * This way, the SigmaCommonInput instances will appear twice -
		 * once in the array and once in the corresponding SigmaProverInput. 
		 * This is an undesired duplication and redundancy, So we decided to implement using the 
		 * first way, although this is less efficient.
		 * In case the efficiency is important, a user can derive this class and override this implementation.
		 */
		
		SigmaCommonInput[] inputArr = new SigmaCommonInput[2];
		inputArr[b] = proverInput.getCommonParams();
		inputArr[1-b] = simulatorInput;
		return new SigmaORTwoCommonInput(inputArr);
	}

}
