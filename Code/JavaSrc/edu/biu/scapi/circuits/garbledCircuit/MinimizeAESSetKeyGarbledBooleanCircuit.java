package edu.biu.scapi.circuits.garbledCircuit;

import java.io.Serializable;
import java.security.InvalidKeyException;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import edu.biu.scapi.circuits.circuit.BooleanCircuit;
import edu.biu.scapi.circuits.circuit.Gate;
import edu.biu.scapi.primitives.prf.PseudorandomFunction;
import edu.biu.scapi.primitives.prf.cryptopp.CryptoPpAES;
import edu.biu.scapi.circuits.encryption.AES128MultiKeyEncryption;
import edu.biu.scapi.circuits.encryption.KeyNotSetException;
import edu.biu.scapi.circuits.encryption.MultiKeyEncryptionScheme;
import edu.biu.scapi.circuits.encryption.PlaintextTooLongException;
import edu.biu.scapi.circuits.encryption.TweakNotSetException;

/**
 * Our code is designed as such that in its constructor
 * {@link StandardGarbledBooleanCircuit} constructs {@code StandardGarbledGate}s.
 * Each {@code StandardGarbledGate} garbled itself by creating a garbled turth
 * table. The garble dtruth table is created row by row. Thus, if we use
 * {@link AES128MultiKeyEncryption} first we will garbled the first row , then
 * the second row etc.. Each row will require two AES operations and two setKey
 * operations--i.e. the key will be set to the garbled value for each row.
 * <p>
 * However, AES set key operations are expensive, and different rows of the
 * truth tables use the same keys. Consider a 2 input gate. There are a total of
 * four keys(a 0 and a 1 for each wire). Yet if we use
 * {@code StandardGarbledBooleanCircuit} with {@code AES128MultiKeyEncryption} we
 * will perform a total of 8 setKey operations. If we garbled the entire truth
 * table together however, we would be able to minimize this to 4 operations.
 * 
 * </p>
 * <p>
 * In order to minimize the number of row operations, we have to couple the
 * garbled gate and the encryption scheme. They can no longer be totally
 * separate entities. This presents an issue however, since for reasons of
 * allowing users to easily extend our code and add new encrypyion schemes, we
 * want the encryption schemes to be totally separate from the
 * {@code GarbledGate}s. (See <i>Garbling * Schemes </i> by Mihir Bellare, Viet
 * Tung Hoang, and Phillip Rogaway for their discussion on garbling schemes as
 * an entity in their own right). Therefore, we create the specialized {code
 * {@link MinimizeAESSetKeyGarbledBooleanCircuit} and
 * {@code MinimizeAESSetKeyGarbledGate} to allow us to minimize the number of
 * setKey operations while still in general decoupling garbling encryption
 * schemes from the gates and circuits.
 * </p>
 * <p> The only difference of this class from {@code StandardGarbledBooleanCircuit} is that it uses {@code {@link MinimizeAESSetKeyGarbledGate}}s instead of {@code StandardGarbledGate}s. All of the major differences that we discussed take place in {@link MinimizeAESSetKeyGarbledGate}.
 * <p> Note that currently only the constructor and not the verify method minimizes AES set key calls
 * @author Steven Goldfeder
 * 
 */
public class MinimizeAESSetKeyGarbledBooleanCircuit extends
    AbstractGarbledBooleanCircuit implements Serializable {

  private static final long serialVersionUID = 1L;

  /**
   * Constructs a garbled circuit using {@link AES128MultiKeyEncryption} while
   * minimizing the number od setKey operations performed.
   * 
   * @param ungarbledCircuit
   *          the circuit that we will garble
   * @param mes
   *          The MultiKeyEncryptionScheme that will be used to garble and
   *          compute this circuit.
   * @param allInputWireValues
   *          a map that is passed as a parameter. It should be blank when
   *          passed as a parameter and the constructor will add to it the 0 and
   *          1 SecretKey values for each input Wire. The reason that this is
   *          passed as a parameter and not created here or stored as a field is
   *          because we need the constructing and only the constructing
   *          party(from hereon in Alice) to have access to this. The second
   *          party--i.e. the one that will compute on the circuit(from hereon
   *          in Bob) should not know which input wire value is 0 and which is 1
   *          nor should Bob have access to both the 0 and 1 values. Rather, Bob
   *          is given access to only a single value for each input wire, and he
   *          does not know what this value encodes. Alice gives Bob the
   *          appropriate garbled values for her inputs, and Bob gets the value
   *          for his input from Alice via oblivious transfer. Thus, we have
   *          designed this class so that only Alice will have access to the map
   *          with both values of each input wire.
   *          <p>
   *          Note that there is one case in which Alice will give this map to
   *          Bob: In the case of a malicious adversary, Alice will construct
   *          multiple circuits and Bob will ask Alice to uncover some of them
   *          to verify them(using our verify method. The way that Alice
   *          uncovers these is by giving Bob access to the allInputWireValues
   *          map. Bob calls the verify method and passes this map as well as
   *          the agreed upon(ungarbled) circuit to the verify method to test
   *          that Alice constructed the circuit correctly.
   *          </p>
   *          <p>
   *          See <i>Secure Multiparty Computation for Privacy-Preserving Data
   *          Mining</i> by Yehuda Lindell and Benny Pinkas Section 3 for an
   *          overview of Yao's protocol, and a more in depth explanation of all
   *          that is discussed here.
   *          </p>
   * @throws PlaintextTooLongException
   * @throws TweakNotSetException
   * @throws KeyNotSetException
   * @throws IllegalBlockSizeException
   * @throws InvalidKeyException
   */
  public MinimizeAESSetKeyGarbledBooleanCircuit(
      BooleanCircuit ungarbledCircuit,
      Map<Integer, SecretKey[]> allInputWireValues) throws InvalidKeyException,
      IllegalBlockSizeException, KeyNotSetException, TweakNotSetException,
      PlaintextTooLongException {
    translationTable = new HashMap<Integer, Integer>();
    outputWireLabels = ungarbledCircuit.getOutputWireLabels();
    inputWireLabels = ungarbledCircuit.getInputWireLabels();
    Gate[] ungarbledGates = ungarbledCircuit.getGates();
    numberOfWires = ungarbledCircuit.getNumberOfWires();
    gates = new GarbledGate[ungarbledGates.length];
    Map<Integer, SecretKey[]> allWireValues = new HashMap<Integer, SecretKey[]>();
    Map<Integer, Integer> signalBits = new HashMap<Integer, Integer>();
    SecureRandom random = new SecureRandom();
    
    //this will be passed to the gates for encryption
    PseudorandomFunction aes = new CryptoPpAES();

    /*this will be passed to the gates and used for decryption and (for now) verifying. Eventually, verifying willl
    *also minimze seKEy operations and use aes directly
    */
    MultiKeyEncryptionScheme mes = new AES128MultiKeyEncryption();

    for (int currentWire = 0; currentWire < numberOfWires; currentWire++) {
      /*
       * assign a 0-encoded value and a 1-encoded value for each GarbledWire.
       * These are the two possible values that the given GarbledWire can be set
       * to.
       */
      SecretKey zeroValue = mes.generateKey();
      SecretKey oneValue = mes.generateKey();
      // Assigns a 0 or 1 as the signal bit for the current wire
      int signalBit = random.nextInt(2);
      signalBits.put(currentWire, signalBit);
      // put the signal bits on the wires
      int signalOnZeroValue = signalBit ^ 0;

      if (signalOnZeroValue == 0) {
        // set the signal bit on the 0-value for the wire. This is the last bit
        // of the wire's 0 value(key)
        byte[] value = zeroValue.getEncoded();
        value[value.length - 1] &= 254;
        zeroValue = new SecretKeySpec(value, "");
        // set the signal bit on the 1-value for the wire. This is the last bit
        // of the wire's 1 value(key)
        value = oneValue.getEncoded();
        value[value.length - 1] |= 1;
        oneValue = new SecretKeySpec(value, "");
      } else if (signalOnZeroValue == 1) {
        // // set 0-value signal bit. This is the last bit of the wire's 0
        // value(key)
        byte[] value = zeroValue.getEncoded();
        value[value.length - 1] |= 1;
        zeroValue = new SecretKeySpec(value, "");
        // set the 1-value signal bit. This is the last bit of the wire's 1
        // value(key)
        value = oneValue.getEncoded();
        value[value.length - 1] &= 254;
        oneValue = new SecretKeySpec(value, "");
      }
      // put the 0-value and the 1-value on the allWireValuesMap
      allWireValues.put(currentWire, new SecretKey[] { zeroValue, oneValue });
    }
    /*
     * add both values of input wire labels to the addInputWireLabels Map that
     * was passed as a parameter. See the comments on the parameter to
     * understand the necessity of maintaining this map.
     */
    for (int w : inputWireLabels) {
      allInputWireValues.put(w, allWireValues.get(w));
    }
   /*  now that all wires have garbled values, we create the individual garbled
     gates*/
    for (int i = 0; i < gates.length; i++) {

      //here we use MinimizeAESSetKeyGarbledGate and not StandardGarbledGate
      gates[i] = new MinimizeAESSetKeyGarbledGate(ungarbledGates[i],
          allWireValues, signalBits,mes,aes);
    }
    /*
     * add the output wire labels' signal bits to the translation table. For a
     * full understanding on why we chose to implement the translation table
     * this way, see the documentation to the translationTable field of
     * AbstractGarbledBooleanCircuit
     */
    for (int n : outputWireLabels) {
      translationTable.put(n, signalBits.get(n));
    }
  }
}
