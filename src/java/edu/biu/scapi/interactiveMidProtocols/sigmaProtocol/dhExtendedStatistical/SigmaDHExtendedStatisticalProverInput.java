/**
* %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
* 
* Copyright (c) 2014 - SCAPI (http://crypto.biu.ac.il/scapi)
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
package edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.dhExtendedStatistical;

import java.math.BigInteger;
import java.util.ArrayList;

import edu.biu.scapi.interactiveMidProtocols.sigmaProtocol.utility.SigmaProverInput;

/**
 * Concrete implementation of SigmaProtocol input, used by the SigmaDHExtendedStatisticalProver.<p>
 *
 * Proves knowledge of y (of size N) s.t. y=log_u1 v1=log_u2 v2=...=log_un vn mod N
 * 
 * @author Eindhoven University of Technology (Meilof Veeningen)
 *
 */
public class SigmaDHExtendedStatisticalProverInput implements SigmaProverInput{

	private SigmaDHExtendedStatisticalCommonInput params;
	private BigInteger w;
	
	/**
	 * Sets the input for the prover. <p>
	 * The prover gets w s.t. y=log_u1 v1=log_u2 v2=...=log_un vn mod N
	 * @param gArray  Array of u's
	 * @param hArray  Array of v's
	 * @param w       Value w
	 */
	public SigmaDHExtendedStatisticalProverInput(BigInteger N, ArrayList<BigInteger> gArray, ArrayList<BigInteger> hArray, BigInteger w){
		params = new SigmaDHExtendedStatisticalCommonInput(N, gArray, hArray);
		this.w = w;
	}
	
	public BigInteger getW(){
		return w;
	}
	
	@Override
	public SigmaDHExtendedStatisticalCommonInput getCommonParams() {
		
		return params;
	}  
}
