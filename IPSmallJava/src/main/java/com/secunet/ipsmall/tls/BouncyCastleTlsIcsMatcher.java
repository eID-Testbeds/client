package com.secunet.ipsmall.tls;

import java.util.ArrayList;
import java.util.List;

import com.secunet.bouncycastle.crypto.tls.CipherSuite;
import com.secunet.bouncycastle.crypto.tls.SignatureAndHashAlgorithm;

import com.secunet.ipsmall.log.Logger;
import com.secunet.ipsmall.log.IModuleLogger.LogLevel;
import com.secunet.ipsmall.tobuilder.ics.TLSCipherSuiteType;
import com.secunet.ipsmall.tobuilder.ics.TLSSupportedCurveType;
import com.secunet.ipsmall.tobuilder.ics.TLSSupportedSignatureAlgorithmType;
import com.secunet.ipsmall.tobuilder.ics.TLSVersionType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType;
import com.secunet.ipsmall.tobuilder.ics.TLSchannelType.TLSVersion;
import com.secunet.ipsmall.tobuilder.ics.TR031242ICS;
import com.secunet.testbedutils.utilities.BouncyCastleTlsHelper;

public class BouncyCastleTlsIcsMatcher {
	private TR031242ICS ics = null;

	public BouncyCastleTlsIcsMatcher(TR031242ICS ics) {
		if (ics != null) {
			this.ics = ics;
		} else {
			throw new IllegalArgumentException("Input may not be null.");
		}
	}

	public boolean matchCipherSuites(boolean isTls12Channel, TLSVersionType tlsVersion, int[] clientCipherSuites) {
		// put cipher suite names into list
		ArrayList<String> matchCipherSuites = new ArrayList<String>();
		for (int cipherSuite : clientCipherSuites) {
			if (CipherSuite.isSCSV(cipherSuite))
				continue; // SCSV is not listed in ICS
			String cipherSuiteString = BouncyCastleTlsHelper.convertCipherSuiteIntToString(cipherSuite);
			if (null == cipherSuiteString) {
				cipherSuiteString = "Unknown cipher suite " + cipherSuite;
				Logger.TLS.logState("Found: " + cipherSuiteString, LogLevel.Debug);
			}
			matchCipherSuites.add(cipherSuiteString);
		}

		// iterate over ics and remove all found cipher suites from matching
		// list
		if (ics != null && ics.getSupportedCryptography() != null) {
			// select TLS channel
			TLSchannelType tlsChannel = null;
			if (isTls12Channel) {
				tlsChannel = ics.getSupportedCryptography().getTLSchannel12();
			} else {
				tlsChannel = ics.getSupportedCryptography().getTLSchannel2();
			}
			if (tlsChannel != null) {
				List<TLSVersion> tlsElements = tlsChannel.getTLSVersion();
				if (tlsElements != null) {
					for (TLSVersion tlsElement : tlsElements) {
                        // NOTE: a client always offers a single TLS version, we choose it for ICS matching
						if (tlsElement != null && tlsElement.isEnabled() && tlsElement.getVersion() == tlsVersion) {
							// run over all chiphersuites in tlsElement
							List<TLSCipherSuiteType> cipherSuites = tlsElement.getCipherSuite();
							if (cipherSuites != null) {
								for (TLSCipherSuiteType cipherSuite : cipherSuites) {
									if (cipherSuite != null) {
										String cipherSuiteName = cipherSuite.value();
										if (cipherSuiteName != null && !cipherSuiteName.isEmpty()) {
											// search and remove cipher suite
											// from matching list
											if (matchCipherSuites.contains(cipherSuiteName)) {
												matchCipherSuites.remove(cipherSuiteName);
											} else {
												return false;
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		// if the cipher suites are equal, the matching list must be empty here
		return matchCipherSuites.isEmpty();
	}

    public boolean matchSignatureAndHashAlgorithms(boolean isTls12Channel, TLSVersionType tlsVersion, SignatureAndHashAlgorithm[] signatureAlgorithms) {
        // put signature algorithm names into list
        ArrayList<String> matchSignatureAlgorithms = new ArrayList<String>();
        for (SignatureAndHashAlgorithm signatureAndHashAlgorithm : signatureAlgorithms) {
            String signatureAndHashAlgorithmString = BouncyCastleTlsHelper.convertSignatureAndHashAlgorithmObjectToString(signatureAndHashAlgorithm);
            if (null == signatureAndHashAlgorithmString) {
                signatureAndHashAlgorithmString = "Unknown SignatureAndHashAlgorithm " + signatureAndHashAlgorithm;
                Logger.TLS.logState("Found: " + signatureAndHashAlgorithmString, LogLevel.Debug);
            }
            matchSignatureAlgorithms.add(signatureAndHashAlgorithmString);
        }

        // iterate over ics and remove all found signatureAndHashAlgorithms from matching
        // list, order and quantity of elements will be checked
        if (ics != null && ics.getSupportedCryptography() != null) {
            // select TLS channel
            TLSchannelType tlsChannel = null;
            if (isTls12Channel) {
                tlsChannel = ics.getSupportedCryptography().getTLSchannel12();
            } else {
                tlsChannel = ics.getSupportedCryptography().getTLSchannel2();
            }
            if (tlsChannel != null) {
                List<TLSVersion> tlsElements = tlsChannel.getTLSVersion();
                if (tlsElements != null) {
                    for (TLSVersion tlsElement : tlsElements) {
                        // NOTE: a client always offers a single TLS version, we choose it for ICS matching
                        if (tlsElement != null && tlsElement.getVersion() == tlsVersion) {
                            // run over all SupportedSignatureAlgorithms in tlsElement
                            List<TLSSupportedSignatureAlgorithmType> supportedSignatureAlgorithms = tlsElement.getSupportedSignatureAlgorithm();
                            if (supportedSignatureAlgorithms != null) {
                                if(supportedSignatureAlgorithms.size() != matchSignatureAlgorithms.size()) {
                                    Logger.TLS.logState("Size mismatch, ics: " + supportedSignatureAlgorithms.size() + " received: " + matchSignatureAlgorithms.size(), LogLevel.Debug);
                                    return false;
                                }
                                for (TLSSupportedSignatureAlgorithmType supportedSignatureAlgorithm : supportedSignatureAlgorithms) {
                                    if (supportedSignatureAlgorithm != null) {
                                        String supportedSignatureAlgorithmName = supportedSignatureAlgorithm.value();
                                        if (supportedSignatureAlgorithmName != null && !supportedSignatureAlgorithmName.isEmpty()) {
                                            // search and remove cipher suite
                                            // from matching list
                                            if (matchSignatureAlgorithms.get(0).equals(supportedSignatureAlgorithmName)) {
                                                matchSignatureAlgorithms.remove(0);
                                            } else {
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // if the supportedSignatureAlgorithms are equal, the matching list must be empty here
        return matchSignatureAlgorithms.isEmpty();
    }

    public boolean matchEllipticCurves(boolean isTls12Channel, TLSVersionType tlsVersion, int[] namedCurves) {
        // put curve names into list
        ArrayList<String> matchNamedCurves = new ArrayList<String>();
        for (int namedCurve : namedCurves) {
            String namedCurveString = BouncyCastleTlsHelper.convertNamedCurveIntToString(namedCurve);
            if (null == namedCurveString) {
                namedCurveString = "Unknown NamedCurve " + namedCurve;
                Logger.TLS.logState("Found: " + namedCurveString, LogLevel.Debug);
            }
            matchNamedCurves.add(namedCurveString);
        }

        // iterate over ics and remove all found namedCurves from matching
        // list, order and quantity of elements will be checked
        if (ics != null && ics.getSupportedCryptography() != null) {
            // select TLS channel
            TLSchannelType tlsChannel = null;
            if (isTls12Channel) {
                tlsChannel = ics.getSupportedCryptography().getTLSchannel12();
            } else {
                tlsChannel = ics.getSupportedCryptography().getTLSchannel2();
            }
            if (tlsChannel != null) {
                List<TLSVersion> tlsElements = tlsChannel.getTLSVersion();
                if (tlsElements != null) {
                    for (TLSVersion tlsElement : tlsElements) {
                        // NOTE: a client always offers a single TLS version, we choose it for ICS matching
                        if (tlsElement != null && tlsElement.getVersion() == tlsVersion) {
                            // run over all namedCurves in tlsElement
                            List<TLSSupportedCurveType> supportedCurves = tlsElement.getSupportedCurve();
                            if (supportedCurves != null) {
                                if(supportedCurves.size() != matchNamedCurves.size()) {
                                    Logger.TLS.logState("Size mismatch, ics: " + supportedCurves.size() + " received: " + matchNamedCurves.size(), LogLevel.Debug);
                                    return false;
                                }
                                for (TLSSupportedCurveType supportedCurve : supportedCurves) {
                                    if (supportedCurve != null) {
                                        String supportedCurveName = supportedCurve.value();
                                        if (supportedCurveName != null && !supportedCurveName.isEmpty()) {
                                            // search and remove cipher suite
                                            // from matching list
                                            if (matchNamedCurves.get(0).equals(supportedCurveName)) {
                                                matchNamedCurves.remove(0);
                                            } else {
                                                return false;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // if the namedCurves are equal, the matching list must be empty here
        return matchNamedCurves.isEmpty();
    }
}
