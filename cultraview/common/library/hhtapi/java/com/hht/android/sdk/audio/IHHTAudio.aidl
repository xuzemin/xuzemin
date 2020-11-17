// IHHTAudio.aidl
package com.hht.android.sdk.audio;

// Declare any non-default types here with import statements 26

interface IHHTAudio {
   int getAudioSoundMode();
   int	getAudioSpdifOutMode();
   boolean	getAvcMode();
   int	getBalance();
   int	getBass();
   int	getEarPhoneVolume();
   int	getEqBand10k();
   int	getEqBand120();
   int	getEqBand1500();
   int	getEqBand500();
   int	getEqBand5k();
//   int	getSystemVolumeMode();
   int	getTreble();
   boolean	setAudioSoundMode(int soundMode);
   boolean	setAudioSpdifOutMode(int spdifMode);
   boolean	setAvcMode(boolean isAvcEnable);
   boolean	setBalance(int balanceValue);
   boolean	setBass(int bassValue);
   boolean	setEarPhoneVolume(int volume);
   boolean	setEqBand10k(int eqValue);
   boolean	setEqBand120(int eqValue);
   boolean	setEqBand1500(int eqValue);
   boolean	setEqBand500(int eqValue);
   boolean	setEqBand5k(int eqValue);
//   boolean	setSystemVolumeMode(String strMode);
   boolean	setTreble(int bassValue);
   boolean setSourceVolumeMute(boolean enable);
   boolean getSourceVolumeMute();
}
