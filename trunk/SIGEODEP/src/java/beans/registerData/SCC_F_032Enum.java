/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.registerData;

/**
 *
 * @author SANTOS
 */
public enum SCC_F_032Enum {
  registro,
  clave,
  instisal,
  hiscli,
  apellid1,
  apellid2,
  nombre1,
  nombre2,
  tid,
  nid,
  medad,
  edadcantid,
  sexo,
  ocupa,
  asegu,
  despl,
  disca,
  getnico,
  cual,
  deptor,
  codigodepa,
  municres,
  codigomuni,
  baveres,
  codigobarr,
  dirres,
  telres,
  deptoe,
  codigodeld,
  mpio,
  codmunic,
  baveevn,
  codbar,
  direv,
  dia,
  mes,
  ao,
  fechaev,
  diaev,
  horas,
  minutos,
  ampm,
  horaev,
  dia1,
  mes1,
  ao1,
  fechacon,
  diacon,
  horas1,
  minutos1,
  ampm1,
  horacon,
  remitido,
  deqips,
  intenci,
  ointenci,
  lugar,
  olugar,
  activi,
  oactivi,
  mecobj,
  altura,
  cpolvora,
  cdntrl,
  omecobj,
  alcohol,
  drogas,
  gradogra,
  porcent,
  ttrans,
  tcontp,
  totrans,
  tocontp,
  tusuar,
  tosuar,
  tsegu,
  cintu,
  cascom,
  cascob,
  chale,
  otroel,
  cualoe,
  anteca,
  relacav,
  contex,
  orelaci,
  sexa,
  intpre,
  trment,
  fprec,
  ofactor,
  sistem,
  lacera,
  craneo,
  cortada,
  ojos,
  lesprof,
  maxilof,
  esglux,
  cuello,
  fractura,
  torax,
  quemadur,
  abdomen,
  contusi,
  columna,
  orgsist,
  pelvis,
  tce,
  msup,
  olesi,
  minf,
  colesi,
  ositio,
  nosabe,
  cotro,
  ma1,
  ag1,
  ma2,
  ag2,
  ma3,
  ag3,
  ma4,
  ag4,
  ma5,
  ag5,
  ma6,
  ag6,
  ma7,
  ag7,
  ag8,
  ag9,
  destino,
  refer,
  cie10,
  txtcie101,
  cie102,
  txtcie102,
  cie103,
  txtcie103,
  cie104,
  txtcie104,
  medico,
  digita,
  NOVALUE;
  public static SCC_F_032Enum convert(String str)
    {
        try {
            return valueOf(str);
        } 
        catch (IllegalArgumentException ex) {
            return NOVALUE;
        }
    }  
}
