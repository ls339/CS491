package cs491;

public class PseudoApp1 {
	/* 
	 * Input: DOF interface is the default interface, end2endsec is the interface type  
	 * Output: A session object providing a secure end-to-end connection
	 * 
	 * beginSession() {
	 * 
	 * Resolve - Saad and Alex
	 * Previously agreed upon primeNumber & primeModulo
	 * primeNumber ^ [privateKey] mod primeModulo = PublicKey(s)
	 * PublicKey ^ [privateKey] mod primeModulo	= SharedKey					//////modulo <- inputModulo()//////
	 * 
	 * 																		//////randomNumber <- rand()//////
	 * 
	 *								
	 * ProviderGenerateKeyPair()	//Provider generates public and private key
	 * 								//Provider sends public key to Requester					//DOF takes object with keys
	 * 								//Requester receives public key								//DOF sends object with keys
	 * RequesterGenerateKeyPair		//Requester generates private key
	 * 								//Requester sends public key to Provider					//DOF sends object with key
	 *								//Both parties generate shared secret						//Diffie-Hellman achieved
	 * 																							//Parties begin Encryption
	 *								//Data Transformation occurs for all sent and received data
	 *								//Shared secret is used to transform future data			//Encryption and Decryption
	 *
	 *								//Requester ends the session
	 * }
	 */
	

}
