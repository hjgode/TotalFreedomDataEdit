package hsm.demo.totalfreedom;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Created by E841719 on 18.01.2017.
 */

public class AimId {
    public String aimid;
    public String comment;

    public AimId(String a, String c){
        aimid=a;
        comment=c;
    }

    public String toString(){
        return comment;
    }

    public String getAimId(){
        return aimid;
    }

    public static AimId AimIds[]={
            new AimId("]d2", "GS1 DataMatrix"),
            new AimId("]C1", "GS1 128 symbol"),
            new AimId("]e0", "GS1 Databar"),
            new AimId("]I0", "ITF14, check digit was not transmitted by the scanner"),
            new AimId("]I1", "GS1 ITF14, check digit has been validated and transmitted by the scanner"),
            new AimId("]E0", "EAN-8, EAN-13, UPC-A or UPC-E"),
            new AimId("]E1", "Two digit Add-On symbol"),
            new AimId("]E2", "Five digit Add-On symbol"),
            new AimId("]E3", "EAN-13, UPC-A or UPC-E with Add-On symbol"),
            new AimId("]E4", "EAN-8"),
            new AimId("]A0", "Code-39, No check character or Full ASCII"),
            new AimId("]A1", "Code-39, Reader has performed mod 43 check"),
            new AimId("]A2", "Code-39, Reader has performed mod 43 check and stripped the check character"),
            new AimId("]A4", "Code-39, Reader has performed Full ASCII conversion"),
            new AimId("]B0", "Telepen, Full ASCII mode"),
            new AimId("]B1", "Telepen, Double density numeric mode"),
            new AimId("]B2", "Telepen, Double density numeric followed by full ASCII"),
            new AimId("]B4", "Telepen, Full ASCII followed by double density numeric"),
            new AimId("]C0", "Code 128, Standard"),
            new AimId("]C1", "Code 128, Function code 1 in first character position"),
            new AimId("]C2", "Code 128, Function code 2 in second character position"),
            new AimId("]C4", "Code 128, Concatenation according to ISBT specification has been performed, and concatenated data follows."),
            new AimId("]D", "Code One"),
            new AimId("]E", "EAN/UPC"),
            new AimId("]F", "Codabar"),
            new AimId("]G", "Code 93"),
            new AimId("]H", "Code 11"),
            new AimId("]I", "ITF (Interleaved Two of Five)"),
            new AimId("]K", "Code16K"),
            new AimId("]L0", "PDF417/Micro417, Reader set to conform with protocol defined in 1994 PDF417 symbology specifications. When this option is trasmitted, the receiver connot determine reliably whether ECIs have bebb invoked, nor whether data byte 92 has been doubled in transmission."),
            new AimId("]L1", "PDF417/Micro417, Reader set follow the protocol of this standard for Exctended Channel Interpretation. All data characters 92 are doubled."),
            new AimId("]L2", "PDF417/Micro417, Reader set follow the protocol of this standard for Basic Chanel operation. Data characters 92 are not doubled. When decoders are set to this mode, unbuffered Structued Append symbols and symbols requiring the decored by convey ECI sequences cannot be transmitted."),
            new AimId("]L3", "PDF417/Micro417, Code 128 emulation : implied FNC1 in first position"),
            new AimId("]L4", "PDF417/Micro417, Code 128 emulation : implied FNC1 after initial letter or pair odf digits"),
            new AimId("]L5", "PDF417/Micro417, Code 128 emulation: no implied FNC1"),
            new AimId("]M", "MSI code"),
            new AimId("]N", "Anker code"),
            new AimId("]O", "CodaBlock"),
            new AimId("]P", "Plessey code"),
            new AimId("]R", "Straight 2 of 5 (two bar start/stop codes)"),
            new AimId("]S", "Straight 2 of 5 (three bar start/stop codes)"),
            new AimId("]T", "Code 49"),
            new AimId("]X", "Other code"),
            new AimId("]Z", "No barcode data"),
            new AimId("]z0", "Aztec, No options"),
            new AimId("]z1", "Aztec, FNC1 preceeding 1st message character"),
            new AimId("]z2", "Aztec, FNC1 following an initial letter or pair of digits"),
            new AimId("]z3", "Aztec, ECI protocol implemented"),
            new AimId("]z4", "Aztec, FNC1 preceeding 1st message chracter, ECI protocol implemented"),
            new AimId("]z5", "Aztec, FNC1 following an initial letter or pair of digits, ECI protocol implemented"),
            new AimId("]z6", "Aztec, Structured append header included"),
            new AimId("]z7", "Aztec, Structured append header included, FNC1 preceeding 1st message character"),
            new AimId("]z8", "Aztec, Structured append header included, FNC1 following an initial letter or pair of digits"),
            new AimId("]z9", "Aztec, Structured append header included, ECI protocol implemented"),
            new AimId("]zA", "Aztec, Structured append header included, FNC1 preceeding 1st message character, ECI protocol implemented"),
            new AimId("]zB", "Aztec, Structured append header included, FNC1 following an initial letter or pair of digits, ECI protocol implemented"),
            new AimId("]zC", "Aztec, Aztec \"Rune\" decoded"),
    };
}


