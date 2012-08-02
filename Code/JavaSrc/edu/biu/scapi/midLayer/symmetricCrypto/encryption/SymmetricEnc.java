package edu.biu.scapi.midLayer.symmetricCrypto.encryption;

import java.security.InvalidKeyException;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.InvalidParameterSpecException;

import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;

import edu.biu.scapi.midLayer.ciphertext.SymmetricCiphertext;
import edu.biu.scapi.midLayer.plaintext.Plaintext;
import edu.biu.scapi.securityLevel.Eav;
import edu.biu.scapi.securityLevel.Indistinguishable;

/**
 * This is the main interface for the Symmetric Encryption family.<p> 
 * The symmetric encryption family of classes implements three main functionalities that correspond to the cryptographer�s language 
 * in which an encryption scheme is composed of three algorithms:<p>
 * 	1.	Generation of the key.<p>
 *	2.	Encryption of the plaintext.<p>
 *	3.	Decryption of the ciphertext.<p>
 * 
 * Any symmetric encryption scheme belongs by default at least to the Eavsdropper Security Level and to the Indistinguishable Security Level.
 *   
 * @author Cryptography and Computer Security Research Group Department of Computer Science Bar-Ilan University (Yael Ejgenberg)
 *
 */
public interface SymmetricEnc extends Eav, Indistinguishable{
	/**
	 * Sets the secret key for this symmetric encryption.
	 * The key can be changed at any time. 
	 * @param secretKey secret key.
	 * @throws InvalidKeyException if the given key does not match this encryption scheme.
	 */
	public void setKey(SecretKey secretKey) throws InvalidKeyException;
	
	/**
	 * An object trying to use an instance of symmetric encryption needs to check if it has already been initialized.
	 * @return true if the object was initialized by calling the function setKey.
	 */
	public boolean isKeySet();
	
	/**
	 * Returns the name of this symmetric encryption.
	 * @return the name of this symmetric encryption.
	 */
	public String getAlgorithmName();
	
	/**
	 * Generates a secret key to initialize this symmetric encryption.
	 * @param keyParams algorithmParameterSpec contains  parameters for the key generation of this symmetric encryption.
	 * @return the generated secret key.
	 * @throws InvalidParameterSpecException if the given keyParams does not match this symmetric encryption.
	 */
	public SecretKey generateKey(AlgorithmParameterSpec keyParams) throws InvalidParameterSpecException;
	
	/**
	 * Generates a secret key to initialize this symmetric encryption.
	 * @param keySize is the required secret key size in bits.
	 * @return the generated secret key.
	 */
	public SecretKey generateKey(int keySize);
	
	/**
	 * Encrypts a plaintext. It lets the system choose the random IV.
	 * @param plaintext
	 * @return  an IVCiphertext, which contains the IV used and the encrypted data.
	 * @throws IllegalStateException if no secret key was set.
	 * @throws IllegalArgumentException if the given plaintext does not match this encryption scheme.
	 */
	public SymmetricCiphertext encrypt(Plaintext plaintext);
	
	/**
	 * This function encrypts a plaintext. It lets the user choose the random IV.
	 * @param plaintext
	 * @param iv random bytes to use in the encryption pf the message.
	 * @return an IVCiphertext, which contains the IV used and the encrypted data. 
	 * @throws IllegalStateException if no secret key was set.
	 * @throws IllegalArgumentException if the given plaintext does not match this encryption scheme.
	 * @throws IllegalBlockSizeException if the given IV length is not as the block size.
	 */
	public SymmetricCiphertext encrypt(Plaintext plaintext, byte[] iv)throws IllegalBlockSizeException;
	
	/**
	 * This function performs the decryption of a ciphertext returning the corresponding decrypted plaintext.
	 * @param ciphertext The Ciphertext to decrypt. 
	 * @return the decrypted plaintext.
	 * @throws IllegalArgumentException if the given ciphertext does not match this encryption scheme.
	 * @throws IllegalStateException if no secret key was set.
	 */
	public Plaintext decrypt(SymmetricCiphertext ciphertext);
	
	
}
