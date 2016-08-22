package org.abia.Agents.NeuralNetworkAgent;

import org.abia.AgentContainer.*;
import org.abia.Agents.BovespaAgent.*;
import org.abia.Agents.TechnicalAnalisysAgent.*;
import org.abia.Blackboard.*;
import org.abia.Blackboard.PostgreSQL.*;
import org.abia.utils.BackpropagationNeuralNetwork.*;

import java.util.*;

public class NeuralNetworkAgent extends AnalisysAgent {
    protected RedeNeural   theNeuralNetwork;
    protected Pregao       LastPregao;
    protected Vector       Pregao;
    protected Vector       TrainingSet=null;
	protected Vector       ProcessedTrainingSet=null;
	protected int          PeriodoMinimo=16;
	protected int          TrainingSetMinimalSize=70;
    protected HashMap      ValoresFixos;
	protected boolean      Simulado=true;
    
    protected void PopularValoresFixos(){
		ValoresFixos.put(new Integer(20010103),new Double(1.07611021069692));
		ValoresFixos.put(new Integer(20010104),new Double(1.00457858907163));
		ValoresFixos.put(new Integer(20010105),new Double(0.984107946026986));
		ValoresFixos.put(new Integer(20010108),new Double(1.00926264472882));
		ValoresFixos.put(new Integer(20010109),new Double(1.02499698104094));
		ValoresFixos.put(new Integer(20010110),new Double(0.996642318567389));
		ValoresFixos.put(new Integer(20010111),new Double(1.00620604054613));
		ValoresFixos.put(new Integer(20010112),new Double(0.989779135338346));
		ValoresFixos.put(new Integer(20010115),new Double(1.00670623145401));
		ValoresFixos.put(new Integer(20010116),new Double(0.985733655603372));
		ValoresFixos.put(new Integer(20010117),new Double(1.02810836672448));
		ValoresFixos.put(new Integer(20010118),new Double(1.01919609097784));
		ValoresFixos.put(new Integer(20010119),new Double(1.00051366931111));
		ValoresFixos.put(new Integer(20010122),new Double(0.992127780946948));
		ValoresFixos.put(new Integer(20010123),new Double(1.02529898804048));
		ValoresFixos.put(new Integer(20010124),new Double(0.996635262449529));
		ValoresFixos.put(new Integer(20010126),new Double(1.00658338960162));
		ValoresFixos.put(new Integer(20010129),new Double(0.999664598356532));
		ValoresFixos.put(new Integer(20010130),new Double(0.991052955320696));
		ValoresFixos.put(new Integer(20010131),new Double(0.997178807199684));
		ValoresFixos.put(new Integer(20010201),new Double(0.964126068013354));
		ValoresFixos.put(new Integer(20010202),new Double(0.992722577616057));
		ValoresFixos.put(new Integer(20010205),new Double(0.989122080993201));
		ValoresFixos.put(new Integer(20010206),new Double(1.01649632418863));
		ValoresFixos.put(new Integer(20010207),new Double(0.988534133004057));
		ValoresFixos.put(new Integer(20010208),new Double(1.02563645015465));
		ValoresFixos.put(new Integer(20010209),new Double(0.993910572406194));
		ValoresFixos.put(new Integer(20010212),new Double(0.987104679659237));
		ValoresFixos.put(new Integer(20010213),new Double(1.01058107229414));
		ValoresFixos.put(new Integer(20010214),new Double(1.00140383715489));
		ValoresFixos.put(new Integer(20010215),new Double(0.989310747663551));
		ValoresFixos.put(new Integer(20010216),new Double(0.959969297986656));
		ValoresFixos.put(new Integer(20010219),new Double(0.987822129282244));
		ValoresFixos.put(new Integer(20010220),new Double(0.990660606437955));
		ValoresFixos.put(new Integer(20010221),new Double(0.980076676513104));
		ValoresFixos.put(new Integer(20010222),new Double(1.0202642041811));
		ValoresFixos.put(new Integer(20010223),new Double(1.01558768070396));
		ValoresFixos.put(new Integer(20010228),new Double(0.983475677682882));
		ValoresFixos.put(new Integer(20010301),new Double(1.03310049713674));
		ValoresFixos.put(new Integer(20010302),new Double(1.00998964488031));
		ValoresFixos.put(new Integer(20010305),new Double(0.9973463602919));
		ValoresFixos.put(new Integer(20010306),new Double(0.987119791981617));
		ValoresFixos.put(new Integer(20010307),new Double(1.00434942416074));
		ValoresFixos.put(new Integer(20010308),new Double(0.989752973467521));
		ValoresFixos.put(new Integer(20010309),new Double(0.993590928699082));
		ValoresFixos.put(new Integer(20010312),new Double(0.963034174781368));
		ValoresFixos.put(new Integer(20010313),new Double(1.00367102466671));
		ValoresFixos.put(new Integer(20010314),new Double(0.978246919917865));
		ValoresFixos.put(new Integer(20010315),new Double(0.987930469006232));
		ValoresFixos.put(new Integer(20010316),new Double(1.01168581103512));
		ValoresFixos.put(new Integer(20010319),new Double(0.973682483428496));
		ValoresFixos.put(new Integer(20010320),new Double(1.00451604205985));
		ValoresFixos.put(new Integer(20010321),new Double(0.996644970811246));
		ValoresFixos.put(new Integer(20010322),new Double(0.947081397697435));
		ValoresFixos.put(new Integer(20010323),new Double(1.02616051752328));
		ValoresFixos.put(new Integer(20010326),new Double(1.0191894700381));
		ValoresFixos.put(new Integer(20010327),new Double(1.00836052202284));
		ValoresFixos.put(new Integer(20010328),new Double(0.986316144253455));
		ValoresFixos.put(new Integer(20010329),new Double(0.976079825041006));
		ValoresFixos.put(new Integer(20010330),new Double(1.01092283993838));
		ValoresFixos.put(new Integer(20010402),new Double(0.968347416539687));
		ValoresFixos.put(new Integer(20010403),new Double(0.982547743365997));
		ValoresFixos.put(new Integer(20010404),new Double(1.00858993957924));
		ValoresFixos.put(new Integer(20010405),new Double(1.04243955250812));
		ValoresFixos.put(new Integer(20010406),new Double(1.002907983106));
		ValoresFixos.put(new Integer(20010409),new Double(1.01615464273386));
		ValoresFixos.put(new Integer(20010410),new Double(1.02235206196073));
		ValoresFixos.put(new Integer(20010411),new Double(0.984782030834662));
		ValoresFixos.put(new Integer(20010412),new Double(1.01012214049531));
		ValoresFixos.put(new Integer(20010416),new Double(0.964994321597969));
		ValoresFixos.put(new Integer(20010417),new Double(0.992523364485981));
		ValoresFixos.put(new Integer(20010418),new Double(1.0431052521448));
		ValoresFixos.put(new Integer(20010419),new Double(0.964961551320629));
		ValoresFixos.put(new Integer(20010420),new Double(0.949067978657058));
		ValoresFixos.put(new Integer(20010423),new Double(1.01438376168224));
		ValoresFixos.put(new Integer(20010424),new Double(1.01259627150363));
		ValoresFixos.put(new Integer(20010425),new Double(1.0067529144157));
		ValoresFixos.put(new Integer(20010426),new Double(1.04194026689261));
		ValoresFixos.put(new Integer(20010427),new Double(1.01158772108152));
		ValoresFixos.put(new Integer(20010430),new Double(0.99933011789925));
		ValoresFixos.put(new Integer(20010502),new Double(0.998592304598472));
		ValoresFixos.put(new Integer(20010503),new Double(1.01644626434853));
		ValoresFixos.put(new Integer(20010504),new Double(0.996763967771761));
		ValoresFixos.put(new Integer(20010507),new Double(0.985556218114358));
		ValoresFixos.put(new Integer(20010508),new Double(0.991394957983193));
		ValoresFixos.put(new Integer(20010509),new Double(1.0021699328677));
		ValoresFixos.put(new Integer(20010510),new Double(1.01448000541309));
		ValoresFixos.put(new Integer(20010511),new Double(0.966651103848463));
		ValoresFixos.put(new Integer(20010514),new Double(0.982336300282895));
		ValoresFixos.put(new Integer(20010515),new Double(0.999227365315727));
		ValoresFixos.put(new Integer(20010516),new Double(1.03430338816252));
		ValoresFixos.put(new Integer(20010517),new Double(1.00543699877668));
		ValoresFixos.put(new Integer(20010518),new Double(1.00608354738407));
		ValoresFixos.put(new Integer(20010521),new Double(1.01639344262295));
		ValoresFixos.put(new Integer(20010522),new Double(0.980235325224749));
		ValoresFixos.put(new Integer(20010523),new Double(0.990761346011194));
		ValoresFixos.put(new Integer(20010524),new Double(0.988497141301389));
		ValoresFixos.put(new Integer(20010525),new Double(0.985333608758521));
		ValoresFixos.put(new Integer(20010528),new Double(0.999440950384347));
		ValoresFixos.put(new Integer(20010529),new Double(1.010627884212));
		ValoresFixos.put(new Integer(20010530),new Double(1.00242147502421));
		ValoresFixos.put(new Integer(20010531),new Double(1.0111118779764));
		ValoresFixos.put(new Integer(20010601),new Double(1.00969283276451));
		ValoresFixos.put(new Integer(20010604),new Double(1.02109248242293));
		ValoresFixos.put(new Integer(20010605),new Double(1.01926641949153));
		ValoresFixos.put(new Integer(20010606),new Double(0.995712893796687));
		ValoresFixos.put(new Integer(20010607),new Double(1.00880683671472));
		ValoresFixos.put(new Integer(20010608),new Double(0.993662700465598));
		ValoresFixos.put(new Integer(20010611),new Double(0.990758818169986));
		ValoresFixos.put(new Integer(20010612),new Double(0.996650026274304));
		ValoresFixos.put(new Integer(20010613),new Double(1.01515850523957));
		ValoresFixos.put(new Integer(20010615),new Double(0.972862429396871));
		ValoresFixos.put(new Integer(20010618),new Double(0.957957957957958));
		ValoresFixos.put(new Integer(20010619),new Double(1.00320445837687));
		ValoresFixos.put(new Integer(20010620),new Double(1.01180473578224));
		ValoresFixos.put(new Integer(20010621),new Double(1.01729462631254));
		ValoresFixos.put(new Integer(20010622),new Double(0.990487755515078));
		ValoresFixos.put(new Integer(20010625),new Double(0.990328293148072));
		ValoresFixos.put(new Integer(20010626),new Double(0.994497936726272));
		ValoresFixos.put(new Integer(20010627),new Double(0.989488243430152));
		ValoresFixos.put(new Integer(20010628),new Double(1.00580095051719));
		ValoresFixos.put(new Integer(20010629),new Double(1.01174345076784));
		ValoresFixos.put(new Integer(20010702),new Double(0.999656593406593));
		ValoresFixos.put(new Integer(20010703),new Double(0.98612160769495));
		ValoresFixos.put(new Integer(20010704),new Double(0.979307461854665));
		ValoresFixos.put(new Integer(20010705),new Double(0.999430848036426));
		ValoresFixos.put(new Integer(20010706),new Double(0.98996298405467));
		ValoresFixos.put(new Integer(20010710),new Double(0.975767599050838));
		ValoresFixos.put(new Integer(20010711),new Double(1.01783345615328));
		ValoresFixos.put(new Integer(20010712),new Double(1.00752968433246));
		ValoresFixos.put(new Integer(20010713),new Double(1.0116412762288));
		ValoresFixos.put(new Integer(20010716),new Double(0.981034237817872));
		ValoresFixos.put(new Integer(20010717),new Double(1.02592136702628));
		ValoresFixos.put(new Integer(20010718),new Double(0.973322041075588));
		ValoresFixos.put(new Integer(20010719),new Double(0.997897179319846));
		ValoresFixos.put(new Integer(20010720),new Double(1.02397907280918));
		ValoresFixos.put(new Integer(20010723),new Double(0.998296906045984));
		ValoresFixos.put(new Integer(20010724),new Double(0.976542507819164));
		ValoresFixos.put(new Integer(20010725),new Double(1.01586839423497));
		ValoresFixos.put(new Integer(20010726),new Double(0.989538549727716));
		ValoresFixos.put(new Integer(20010727),new Double(1.00724112961622));
		ValoresFixos.put(new Integer(20010730),new Double(0.985190510424155));
		ValoresFixos.put(new Integer(20010731),new Double(1.00364856976065));
		ValoresFixos.put(new Integer(20010801),new Double(0.99920023265959));
		ValoresFixos.put(new Integer(20010802),new Double(1.00582114531034));
		ValoresFixos.put(new Integer(20010803),new Double(1.00108514794184));
		ValoresFixos.put(new Integer(20010806),new Double(1.01510333863275));
		ValoresFixos.put(new Integer(20010808),new Double(0.990887734035737));
		ValoresFixos.put(new Integer(20010809),new Double(0.993893239456857));
		ValoresFixos.put(new Integer(20010810),new Double(1.00585513951135));
		ValoresFixos.put(new Integer(20010813),new Double(0.989651455264103));
		ValoresFixos.put(new Integer(20010814),new Double(1.0042117493283));
		ValoresFixos.put(new Integer(20010815),new Double(0.987706992551884));
		ValoresFixos.put(new Integer(20010817),new Double(0.954974741928399));
		ValoresFixos.put(new Integer(20010820),new Double(1.00544311560871));
		ValoresFixos.put(new Integer(20010821),new Double(0.982996568814335));
		ValoresFixos.put(new Integer(20010822),new Double(1.00465404902265));
		ValoresFixos.put(new Integer(20010823),new Double(0.984481161210624));
		ValoresFixos.put(new Integer(20010824),new Double(1.01960630538781));
		ValoresFixos.put(new Integer(20010827),new Double(0.999615414198908));
		ValoresFixos.put(new Integer(20010828),new Double(1.00169282856263));
		ValoresFixos.put(new Integer(20010829),new Double(1.00453218620372));
		ValoresFixos.put(new Integer(20010830),new Double(0.985853024393974));
		ValoresFixos.put(new Integer(20010831),new Double(0.996044058330748));
		ValoresFixos.put(new Integer(20010903),new Double(0.996807102250604));
		ValoresFixos.put(new Integer(20010904),new Double(0.997421875));
		ValoresFixos.put(new Integer(20010905),new Double(0.986214459152503));
		ValoresFixos.put(new Integer(20010906),new Double(0.973314272099118));
		ValoresFixos.put(new Integer(20010910),new Double(0.972827417380661));
		ValoresFixos.put(new Integer(20010917),new Double(0.884918637812448));
		ValoresFixos.put(new Integer(20010918),new Double(1.00037914691943));
		ValoresFixos.put(new Integer(20010919),new Double(1.01809740382793));
		ValoresFixos.put(new Integer(20010920),new Double(0.981200558399255));
		ValoresFixos.put(new Integer(20010921),new Double(0.988238641752822));
		ValoresFixos.put(new Integer(20010924),new Double(1.010941549093));
		ValoresFixos.put(new Integer(20010925),new Double(0.971138327162252));
		ValoresFixos.put(new Integer(20010926),new Double(0.978199237462117));
		ValoresFixos.put(new Integer(20010927),new Double(1.03987607435539));
		ValoresFixos.put(new Integer(20010928),new Double(1.02220086496877));
		ValoresFixos.put(new Integer(20011001),new Double(0.987307258367807));
		ValoresFixos.put(new Integer(20011002),new Double(0.98571564612894));
		ValoresFixos.put(new Integer(20011004),new Double(0.972079992271278));
		ValoresFixos.put(new Integer(20011005),new Double(1.0134168157424));
		ValoresFixos.put(new Integer(20011008),new Double(0.989997057958223));
		ValoresFixos.put(new Integer(20011009),new Double(1.01872213967311));
		ValoresFixos.put(new Integer(20011010),new Double(1.0173084402956));
		ValoresFixos.put(new Integer(20011011),new Double(1.03087363792774));
		ValoresFixos.put(new Integer(20011015),new Double(1.05044042651831));
		ValoresFixos.put(new Integer(20011016),new Double(0.993732897872716));
		ValoresFixos.put(new Integer(20011017),new Double(1.00115473441109));
		ValoresFixos.put(new Integer(20011018),new Double(0.97551237689646));
		ValoresFixos.put(new Integer(20011019),new Double(1.0306502955889));
		ValoresFixos.put(new Integer(20011022),new Double(1.03247440875397));
		ValoresFixos.put(new Integer(20011023),new Double(0.992564102564103));
		ValoresFixos.put(new Integer(20011024),new Double(0.987513992938948));
		ValoresFixos.put(new Integer(20011025),new Double(1.02232298569934));
		ValoresFixos.put(new Integer(20011026),new Double(1.00486182190379));
		ValoresFixos.put(new Integer(20011029),new Double(0.96570749511926));
		ValoresFixos.put(new Integer(20011030),new Double(0.968972488353696));
		ValoresFixos.put(new Integer(20011031),new Double(1.03093251088534));
		ValoresFixos.put(new Integer(20011101),new Double(1.00202375714914));
		ValoresFixos.put(new Integer(20011105),new Double(1.06822971548999));
		ValoresFixos.put(new Integer(20011106),new Double(1.02063296341965));
		ValoresFixos.put(new Integer(20011107),new Double(1.01610824742268));
		ValoresFixos.put(new Integer(20011108),new Double(0.995085605580216));
		ValoresFixos.put(new Integer(20011109),new Double(1.01409909192289));
		ValoresFixos.put(new Integer(20011112),new Double(0.987118058282931));
		ValoresFixos.put(new Integer(20011113),new Double(1.02793029362616));
		ValoresFixos.put(new Integer(20011114),new Double(0.992878154513082));
		ValoresFixos.put(new Integer(20011116),new Double(1.00444409792609));
		ValoresFixos.put(new Integer(20011119),new Double(1.00807265388496));
		ValoresFixos.put(new Integer(20011120),new Double(0.973126973126973));
		ValoresFixos.put(new Integer(20011121),new Double(1.01234372527299));
		ValoresFixos.put(new Integer(20011122),new Double(1.01758636861029));
		ValoresFixos.put(new Integer(20011123),new Double(1.03110838005991));
		ValoresFixos.put(new Integer(20011126),new Double(1.02502979737783));
		ValoresFixos.put(new Integer(20011127),new Double(0.98859011627907));
		ValoresFixos.put(new Integer(20011128),new Double(0.95706829375873));
		ValoresFixos.put(new Integer(20011129),new Double(0.978262539365543));
		ValoresFixos.put(new Integer(20011203),new Double(1.04711055276382));
		ValoresFixos.put(new Integer(20011204),new Double(0.985827834433113));
		ValoresFixos.put(new Integer(20011205),new Double(1.01787480033468));
		ValoresFixos.put(new Integer(20011206),new Double(1.01173217755194));
		ValoresFixos.put(new Integer(20011207),new Double(0.98227343230667));
		ValoresFixos.put(new Integer(20011210),new Double(1.01105346266637));
		ValoresFixos.put(new Integer(20011211),new Double(0.992711587089097));
		ValoresFixos.put(new Integer(20011212),new Double(1.01610728198981));
		ValoresFixos.put(new Integer(20011213),new Double(0.969402049694021));
		ValoresFixos.put(new Integer(20011214),new Double(0.985625190142988));
		ValoresFixos.put(new Integer(20011217),new Double(0.996373176942665));
		ValoresFixos.put(new Integer(20011218),new Double(1.03756195786865));
		ValoresFixos.put(new Integer(20011219),new Double(0.992013137269538));
		ValoresFixos.put(new Integer(20011220),new Double(0.972009029345372));
		ValoresFixos.put(new Integer(20011221),new Double(1.03491252515869));
		ValoresFixos.put(new Integer(20011226),new Double(0.999177200987359));
		ValoresFixos.put(new Integer(20011227),new Double(1.02979487947297));
		ValoresFixos.put(new Integer(20011228),new Double(0.987060191916255));
		ValoresFixos.put(new Integer(20020102),new Double(1.02165267344233));
		ValoresFixos.put(new Integer(20020103),new Double(1.02833044982699));
		ValoresFixos.put(new Integer(20020104),new Double(1.00469681037504));
		ValoresFixos.put(new Integer(20020107),new Double(1.00327937482557));
		ValoresFixos.put(new Integer(20020108),new Double(0.98532582237986));
		ValoresFixos.put(new Integer(20020109),new Double(0.989624505928854));
		ValoresFixos.put(new Integer(20020110),new Double(0.967833963340703));
		ValoresFixos.put(new Integer(20020111),new Double(1.00125276344878));
		ValoresFixos.put(new Integer(20020114),new Double(0.96577610951645));
		ValoresFixos.put(new Integer(20020115),new Double(0.991540923639689));
		ValoresFixos.put(new Integer(20020116),new Double(1.00561063715318));
		ValoresFixos.put(new Integer(20020117),new Double(1.01933659431367));
		ValoresFixos.put(new Integer(20020118),new Double(1.00269925770413));
		ValoresFixos.put(new Integer(20020121),new Double(0.98369849697151));
		ValoresFixos.put(new Integer(20020122),new Double(0.988521474724439));
		ValoresFixos.put(new Integer(20020123),new Double(1.01753306674869));
		ValoresFixos.put(new Integer(20020124),new Double(0.994709794437727));
		ValoresFixos.put(new Integer(20020128),new Double(0.987843792736666));
		ValoresFixos.put(new Integer(20020129),new Double(0.961467466543609));
		ValoresFixos.put(new Integer(20020130),new Double(1.00247980161587));
		ValoresFixos.put(new Integer(20020131),new Double(1.01508139163741));
		ValoresFixos.put(new Integer(20020204),new Double(0.983649084191494));
		ValoresFixos.put(new Integer(20020205),new Double(1.01878046831295));
		ValoresFixos.put(new Integer(20020206),new Double(1.00023533103232));
		ValoresFixos.put(new Integer(20020207),new Double(0.994745510156066));
		ValoresFixos.put(new Integer(20020208),new Double(0.993219804478083));
		ValoresFixos.put(new Integer(20020213),new Double(1.02881409747579));
		ValoresFixos.put(new Integer(20020214),new Double(1.0219118895147));
		ValoresFixos.put(new Integer(20020215),new Double(1.00415251038128));
		ValoresFixos.put(new Integer(20020218),new Double(0.986616541353384));
		ValoresFixos.put(new Integer(20020219),new Double(0.990092973632068));
		ValoresFixos.put(new Integer(20020220),new Double(1.02393780788177));
		ValoresFixos.put(new Integer(20020221),new Double(1.01706382019093));
		ValoresFixos.put(new Integer(20020222),new Double(1.00243902439024));
		ValoresFixos.put(new Integer(20020225),new Double(1.03059795030598));
		ValoresFixos.put(new Integer(20020226),new Double(0.998998426098154));
		ValoresFixos.put(new Integer(20020227),new Double(1.01775995416786));
		ValoresFixos.put(new Integer(20020228),new Double(0.98740500985083));
		ValoresFixos.put(new Integer(20020301),new Double(1.02209078600442));
		ValoresFixos.put(new Integer(20020304),new Double(1.00892421390225));
		ValoresFixos.put(new Integer(20020305),new Double(0.96821228664225));
		ValoresFixos.put(new Integer(20020306),new Double(0.987723931196917));
		ValoresFixos.put(new Integer(20020307),new Double(0.991834670135125));
		ValoresFixos.put(new Integer(20020308),new Double(1.01719364709311));
		ValoresFixos.put(new Integer(20020311),new Double(0.980948288210858));
		ValoresFixos.put(new Integer(20020312),new Double(1.03541179906542));
		ValoresFixos.put(new Integer(20020313),new Double(1.00444256399408));
		ValoresFixos.put(new Integer(20020314),new Double(0.991083965178321));
		ValoresFixos.put(new Integer(20020315),new Double(1.01756747184246));
		ValoresFixos.put(new Integer(20020318),new Double(0.991646362687087));
		ValoresFixos.put(new Integer(20020319),new Double(0.991084591084591));
		ValoresFixos.put(new Integer(20020320),new Double(0.99801671624876));
		ValoresFixos.put(new Integer(20020321),new Double(0.973527324343506));
		ValoresFixos.put(new Integer(20020322),new Double(0.970474593570023));
		ValoresFixos.put(new Integer(20020325),new Double(0.997445913461538));
		ValoresFixos.put(new Integer(20020328),new Double(0.998267811417382));
		ValoresFixos.put(new Integer(20020401),new Double(1.01599396454168));
		ValoresFixos.put(new Integer(20020402),new Double(0.983515259523279));
		ValoresFixos.put(new Integer(20020403),new Double(0.987919969799924));
		ValoresFixos.put(new Integer(20020404),new Double(1.02101643102789));
		ValoresFixos.put(new Integer(20020405),new Double(0.993712574850299));
		ValoresFixos.put(new Integer(20020408),new Double(0.991262428442302));
		ValoresFixos.put(new Integer(20020410),new Double(1.01945288753799));
		ValoresFixos.put(new Integer(20020411),new Double(0.999627310673822));
		ValoresFixos.put(new Integer(20020412),new Double(1.02535232272016));
		ValoresFixos.put(new Integer(20020415),new Double(0.979710566504254));
		ValoresFixos.put(new Integer(20020416),new Double(1.01120843230404));
		ValoresFixos.put(new Integer(20020417),new Double(1.00800117448433));
		ValoresFixos.put(new Integer(20020418),new Double(0.988421205942325));
		ValoresFixos.put(new Integer(20020419),new Double(0.993000810432476));
		ValoresFixos.put(new Integer(20020422),new Double(0.981154473957561));
		ValoresFixos.put(new Integer(20020423),new Double(0.997277676950998));
		ValoresFixos.put(new Integer(20020424),new Double(1.01463451622687));
		ValoresFixos.put(new Integer(20020425),new Double(0.991928854345714));
		ValoresFixos.put(new Integer(20020426),new Double(0.985157839222482));
		ValoresFixos.put(new Integer(20020429),new Double(1.00107066381156));
		ValoresFixos.put(new Integer(20020430),new Double(0.999618029029794));
		ValoresFixos.put(new Integer(20020502),new Double(0.958196408100879));
		ValoresFixos.put(new Integer(20020503),new Double(1.00574254267028));
		ValoresFixos.put(new Integer(20020506),new Double(0.985725614591594));
		ValoresFixos.put(new Integer(20020507),new Double(0.992920353982301));
		ValoresFixos.put(new Integer(20020508),new Double(1.0222816399287));
		ValoresFixos.put(new Integer(20020509),new Double(0.95918205595625));
		ValoresFixos.put(new Integer(20020510),new Double(1.00231366716245));
		ValoresFixos.put(new Integer(20020513),new Double(0.989447650453421));
		ValoresFixos.put(new Integer(20020514),new Double(1.01683052824529));
		ValoresFixos.put(new Integer(20020515),new Double(1.01196329072435));
		ValoresFixos.put(new Integer(20020516),new Double(1.0251012145749));
		ValoresFixos.put(new Integer(20020517),new Double(1.00308056872038));
		ValoresFixos.put(new Integer(20020520),new Double(0.997558862902591));
		ValoresFixos.put(new Integer(20020521),new Double(1.00260498894853));
		ValoresFixos.put(new Integer(20020522),new Double(0.97378159200063));
		ValoresFixos.put(new Integer(20020523),new Double(1.01520051746442));
		ValoresFixos.put(new Integer(20020524),new Double(1.00143357757248));
		ValoresFixos.put(new Integer(20020527),new Double(1.00986161921425));
		ValoresFixos.put(new Integer(20020528),new Double(1.00236257678375));
		ValoresFixos.put(new Integer(20020529),new Double(1.02019170333124));
		ValoresFixos.put(new Integer(20020531),new Double(0.990450519830574));
		ValoresFixos.put(new Integer(20020603),new Double(0.984293600808646));
		ValoresFixos.put(new Integer(20020604),new Double(0.995339284303657));
		ValoresFixos.put(new Integer(20020605),new Double(0.999206349206349));
		ValoresFixos.put(new Integer(20020606),new Double(0.962112787926926));
		ValoresFixos.put(new Integer(20020607),new Double(1.01403450837943));
		ValoresFixos.put(new Integer(20020610),new Double(1.02580802735488));
		ValoresFixos.put(new Integer(20020611),new Double(0.969047619047619));
		ValoresFixos.put(new Integer(20020612),new Double(0.993693693693694));
		ValoresFixos.put(new Integer(20020613),new Double(0.985906206214456));
		ValoresFixos.put(new Integer(20020614),new Double(0.978013710081926));
		ValoresFixos.put(new Integer(20020617),new Double(1.02034361911274));
		ValoresFixos.put(new Integer(20020618),new Double(0.990282315489654));
		ValoresFixos.put(new Integer(20020619),new Double(0.97225277049319));
		ValoresFixos.put(new Integer(20020620),new Double(0.949186461324284));
		ValoresFixos.put(new Integer(20020621),new Double(0.953157942982858));
		ValoresFixos.put(new Integer(20020624),new Double(1.03471821504135));
		ValoresFixos.put(new Integer(20020625),new Double(0.995073891625616));
		ValoresFixos.put(new Integer(20020626),new Double(0.998598916495423));
		ValoresFixos.put(new Integer(20020627),new Double(1.03011879150688));
		ValoresFixos.put(new Integer(20020628),new Double(1.01144102424408));
		ValoresFixos.put(new Integer(20020701),new Double(0.977825657599425));
		ValoresFixos.put(new Integer(20020702),new Double(0.995776716856408));
		ValoresFixos.put(new Integer(20020703),new Double(0.980638023234372));
		ValoresFixos.put(new Integer(20020704),new Double(1.00178638585935));
		ValoresFixos.put(new Integer(20020705),new Double(0.987705302674801));
		ValoresFixos.put(new Integer(20020708),new Double(1.01548840744964));
		ValoresFixos.put(new Integer(20020710),new Double(0.98774211659025));
		ValoresFixos.put(new Integer(20020711),new Double(1.02368321333839));
		ValoresFixos.put(new Integer(20020712),new Double(1.0148991301129));
		ValoresFixos.put(new Integer(20020715),new Double(0.96954499863226));
		ValoresFixos.put(new Integer(20020716),new Double(0.994827424057181));
		ValoresFixos.put(new Integer(20020717),new Double(1.01673284174702));
		ValoresFixos.put(new Integer(20020718),new Double(1.00539284053928));
		ValoresFixos.put(new Integer(20020719),new Double(0.978729307315269));
		ValoresFixos.put(new Integer(20020722),new Double(0.934706604932439));
		ValoresFixos.put(new Integer(20020723),new Double(0.985240598463405));
		ValoresFixos.put(new Integer(20020724),new Double(1.01959778370614));
		ValoresFixos.put(new Integer(20020725),new Double(0.972728187581765));
		ValoresFixos.put(new Integer(20020726),new Double(0.953548520587627));
		ValoresFixos.put(new Integer(20020729),new Double(1.00249538895519));
		ValoresFixos.put(new Integer(20020730),new Double(1.01103896103896));
		ValoresFixos.put(new Integer(20020731),new Double(1.04506529651038));
		ValoresFixos.put(new Integer(20020801),new Double(0.999692717402438));
		ValoresFixos.put(new Integer(20020802),new Double(1.0094262295082));
		ValoresFixos.put(new Integer(20020805),new Double(0.961226146975233));
		ValoresFixos.put(new Integer(20020806),new Double(1.03009503695882));
		ValoresFixos.put(new Integer(20020807),new Double(1.01178882624295));
		ValoresFixos.put(new Integer(20020808),new Double(1.0451874366768));
		ValoresFixos.put(new Integer(20020809),new Double(0.968010856921287));
		ValoresFixos.put(new Integer(20020812),new Double(0.973763268576006));
		ValoresFixos.put(new Integer(20020813),new Double(0.971205265322912));
		ValoresFixos.put(new Integer(20020814),new Double(0.989305379076662));
		ValoresFixos.put(new Integer(20020815),new Double(0.982874879588997));
		ValoresFixos.put(new Integer(20020816),new Double(1.03735162800828));
		ValoresFixos.put(new Integer(20020819),new Double(0.988557631744699));
		ValoresFixos.put(new Integer(20020820),new Double(0.983646596580652));
		ValoresFixos.put(new Integer(20020821),new Double(1.01889236748354));
		ValoresFixos.put(new Integer(20020822),new Double(1.02807798262344));
		ValoresFixos.put(new Integer(20020823),new Double(0.997217355457075));
		ValoresFixos.put(new Integer(20020826),new Double(1.04361306324928));
		ValoresFixos.put(new Integer(20020827),new Double(1.02713408595762));
		ValoresFixos.put(new Integer(20020828),new Double(1.00077130736599));
		ValoresFixos.put(new Integer(20020829),new Double(1.00722543352601));
		ValoresFixos.put(new Integer(20020830),new Double(0.993017694882831));
		ValoresFixos.put(new Integer(20020902),new Double(0.999614717780774));
		ValoresFixos.put(new Integer(20020903),new Double(0.976681441510888));
		ValoresFixos.put(new Integer(20020904),new Double(0.986286503551697));
		ValoresFixos.put(new Integer(20020905),new Double(0.97259177753326));
		ValoresFixos.put(new Integer(20020906),new Double(0.999382906510336));
		ValoresFixos.put(new Integer(20020909),new Double(1.02439024390244));
		ValoresFixos.put(new Integer(20020910),new Double(1.00060277275467));
		ValoresFixos.put(new Integer(20020911),new Double(1.02228915662651));
		ValoresFixos.put(new Integer(20020912),new Double(0.999116087212728));
		ValoresFixos.put(new Integer(20020913),new Double(1.00078639536027));
		ValoresFixos.put(new Integer(20020916),new Double(0.965622237501228));
		ValoresFixos.put(new Integer(20020917),new Double(0.981588851591903));
		ValoresFixos.put(new Integer(20020918),new Double(0.984974093264249));
		ValoresFixos.put(new Integer(20020919),new Double(0.986007364544976));
		ValoresFixos.put(new Integer(20020920),new Double(1.02272727272727));
		ValoresFixos.put(new Integer(20020923),new Double(0.966510172143975));
		ValoresFixos.put(new Integer(20020924),new Double(0.987478411053541));
		ValoresFixos.put(new Integer(20020925),new Double(1.008745080892));
		ValoresFixos.put(new Integer(20020926),new Double(0.996857390550498));
		ValoresFixos.put(new Integer(20020927),new Double(0.947494292857919));
		ValoresFixos.put(new Integer(20020930),new Double(0.989329967875172));
		ValoresFixos.put(new Integer(20021001),new Double(1.04348834512351));
		ValoresFixos.put(new Integer(20021002),new Double(0.980217826183596));
		ValoresFixos.put(new Integer(20021003),new Double(1.03628117913832));
		ValoresFixos.put(new Integer(20021004),new Double(1.01312910284464));
		ValoresFixos.put(new Integer(20021007),new Double(0.957127429805616));
		ValoresFixos.put(new Integer(20021008),new Double(0.998194742186619));
		ValoresFixos.put(new Integer(20021009),new Double(0.985079688029841));
		ValoresFixos.put(new Integer(20021010),new Double(1.01732644865175));
		ValoresFixos.put(new Integer(20021011),new Double(0.998759305210918));
		ValoresFixos.put(new Integer(20021014),new Double(0.954376058723885));
		ValoresFixos.put(new Integer(20021015),new Double(1.00662643474145));
		ValoresFixos.put(new Integer(20021016),new Double(0.984013165628306));
		ValoresFixos.put(new Integer(20021017),new Double(1.06343328156732));
		ValoresFixos.put(new Integer(20021018),new Double(1.01348011682768));
		ValoresFixos.put(new Integer(20021021),new Double(1.01174905785857));
		ValoresFixos.put(new Integer(20021022),new Double(1.02223926380368));
		ValoresFixos.put(new Integer(20021023),new Double(1.05454935162362));
		ValoresFixos.put(new Integer(20021024),new Double(0.995833333333333));
		ValoresFixos.put(new Integer(20021025),new Double(1.02204306561894));
		ValoresFixos.put(new Integer(20021028),new Double(0.955966050923615));
		ValoresFixos.put(new Integer(20021029),new Double(1.00282013787341));
		ValoresFixos.put(new Integer(20021030),new Double(1.04874492240392));
		ValoresFixos.put(new Integer(20021031),new Double(1.00983215810905));
		ValoresFixos.put(new Integer(20021101),new Double(0.997246262785208));
		ValoresFixos.put(new Integer(20021104),new Double(0.977613412228797));
		ValoresFixos.put(new Integer(20021105),new Double(0.994754362957732));
		ValoresFixos.put(new Integer(20021106),new Double(0.98397728425109));
		ValoresFixos.put(new Integer(20021107),new Double(1.00989384726373));
		ValoresFixos.put(new Integer(20021108),new Double(1.00622512501276));
		ValoresFixos.put(new Integer(20021111),new Double(1.0026369168357));
		ValoresFixos.put(new Integer(20021112),new Double(0.983309730932632));
		ValoresFixos.put(new Integer(20021113),new Double(1.00442341322909));
		ValoresFixos.put(new Integer(20021114),new Double(1.0122900450635));
		ValoresFixos.put(new Integer(20021118),new Double(1.00880210441117));
		ValoresFixos.put(new Integer(20021119),new Double(1));
		ValoresFixos.put(new Integer(20021120),new Double(1.01173402868318));
		ValoresFixos.put(new Integer(20021121),new Double(1.0199246629659));
		ValoresFixos.put(new Integer(20021122),new Double(1.01117698512975));
		ValoresFixos.put(new Integer(20021125),new Double(0.98481353325644));
		ValoresFixos.put(new Integer(20021126),new Double(0.988873706812415));
		ValoresFixos.put(new Integer(20021127),new Double(1.00927753651796));
		ValoresFixos.put(new Integer(20021128),new Double(1.00127126931351));
		ValoresFixos.put(new Integer(20021129),new Double(1.02636976267214));
		ValoresFixos.put(new Integer(20021202),new Double(1.01570082786183));
		ValoresFixos.put(new Integer(20021203),new Double(0.998969458497283));
		ValoresFixos.put(new Integer(20021205),new Double(0.976554440588952));
		ValoresFixos.put(new Integer(20021206),new Double(1.01498127340824));
		ValoresFixos.put(new Integer(20021209),new Double(0.978238243920901));
		ValoresFixos.put(new Integer(20021210),new Double(0.999226230776671));
		ValoresFixos.put(new Integer(20021211),new Double(1.02739328235408));
		ValoresFixos.put(new Integer(20021212),new Double(0.997456189937818));
		ValoresFixos.put(new Integer(20021213),new Double(0.997827524322282));
		ValoresFixos.put(new Integer(20021216),new Double(1.01959485043544));
		ValoresFixos.put(new Integer(20021217),new Double(1.00575619719617));
		ValoresFixos.put(new Integer(20021218),new Double(1.01403120096003));
		ValoresFixos.put(new Integer(20021219),new Double(1.02030040964952));
		ValoresFixos.put(new Integer(20021220),new Double(1.02516059957173));
		ValoresFixos.put(new Integer(20021223),new Double(0.998259355961706));
		ValoresFixos.put(new Integer(20021226),new Double(0.986748038360942));
		ValoresFixos.put(new Integer(20021227),new Double(0.992578194027213));
		ValoresFixos.put(new Integer(20021230),new Double(1.00302652661563));
		ValoresFixos.put(new Integer(20030102),new Double(1.02973020944267));
		ValoresFixos.put(new Integer(20030103),new Double(0.999741446177713));
		ValoresFixos.put(new Integer(20030106),new Double(1.03620689655172));
		ValoresFixos.put(new Integer(20030107),new Double(0.98801996672213));
		ValoresFixos.put(new Integer(20030108),new Double(0.992421690804985));
		ValoresFixos.put(new Integer(20030109),new Double(1.01069064992364));
		ValoresFixos.put(new Integer(20030113),new Double(1.01662189388852));
		ValoresFixos.put(new Integer(20030114),new Double(1.00536746490504));
		ValoresFixos.put(new Integer(20030116),new Double(0.981683778234086));
		ValoresFixos.put(new Integer(20030117),new Double(0.976907630522088));
		ValoresFixos.put(new Integer(20030120),new Double(0.997601918465228));
		ValoresFixos.put(new Integer(20030121),new Double(0.981713598901099));
		ValoresFixos.put(new Integer(20030122),new Double(0.974376912986445));
		ValoresFixos.put(new Integer(20030123),new Double(1.00179500987255));
		ValoresFixos.put(new Integer(20030127),new Double(0.943379322702025));
		ValoresFixos.put(new Integer(20030128),new Double(0.998765432098765));
		ValoresFixos.put(new Integer(20030129),new Double(1.03289911571741));
		ValoresFixos.put(new Integer(20030130),new Double(0.989689772622664));
		ValoresFixos.put(new Integer(20030131),new Double(1.01767277462562));
		ValoresFixos.put(new Integer(20030203),new Double(0.997166620967005));
		ValoresFixos.put(new Integer(20030205),new Double(0.970852428964253));
		ValoresFixos.put(new Integer(20030206),new Double(0.997545317220544));
		ValoresFixos.put(new Integer(20030207),new Double(0.98249100889646));
		ValoresFixos.put(new Integer(20030210),new Double(1.00953665350159));
		ValoresFixos.put(new Integer(20030211),new Double(1.00276717557252));
		ValoresFixos.put(new Integer(20030212),new Double(1.0000951565325));
		ValoresFixos.put(new Integer(20030213),new Double(0.961750713606089));
		ValoresFixos.put(new Integer(20030214),new Double(0.997328848436882));
		ValoresFixos.put(new Integer(20030217),new Double(1.01071322289455));
		ValoresFixos.put(new Integer(20030218),new Double(1.02561586024144));
		ValoresFixos.put(new Integer(20030219),new Double(0.981052631578947));
		ValoresFixos.put(new Integer(20030220),new Double(0.996586031993757));
		ValoresFixos.put(new Integer(20030221),new Double(1.01115787413135));
		ValoresFixos.put(new Integer(20030224),new Double(0.992546704094473));
		ValoresFixos.put(new Integer(20030225),new Double(0.993953579091086));
		ValoresFixos.put(new Integer(20030226),new Double(0.980671114599686));
		ValoresFixos.put(new Integer(20030227),new Double(1.01310655327664));
		ValoresFixos.put(new Integer(20030228),new Double(1.01530713015998));
		ValoresFixos.put(new Integer(20030305),new Double(1.002431670071));
		ValoresFixos.put(new Integer(20030306),new Double(1.02998253444595));
		ValoresFixos.put(new Integer(20030307),new Double(1.0102684879887));
		ValoresFixos.put(new Integer(20030310),new Double(0.96130175307721));
		ValoresFixos.put(new Integer(20030311),new Double(1.00300708119119));
		ValoresFixos.put(new Integer(20030312),new Double(1.02292069632495));
		ValoresFixos.put(new Integer(20030313),new Double(1.01947622199111));
		ValoresFixos.put(new Integer(20030314),new Double(1.00315311137902));
		ValoresFixos.put(new Integer(20030317),new Double(1.00536193029491));
		ValoresFixos.put(new Integer(20030318),new Double(1.02537931034483));
		ValoresFixos.put(new Integer(20030319),new Double(0.986996681911936));
		ValoresFixos.put(new Integer(20030320),new Double(1.01390150826822));
		ValoresFixos.put(new Integer(20030321),new Double(1.01953580069899));
		ValoresFixos.put(new Integer(20030324),new Double(0.971521490726905));
		ValoresFixos.put(new Integer(20030325),new Double(1.01619469827196));
		ValoresFixos.put(new Integer(20030326),new Double(0.997685185185185));
		ValoresFixos.put(new Integer(20030327),new Double(1.00240942352311));
		ValoresFixos.put(new Integer(20030328),new Double(1.0145108163447));
		ValoresFixos.put(new Integer(20030331),new Double(0.989294489294489));
		ValoresFixos.put(new Integer(20030401),new Double(1.02820649281533));
		ValoresFixos.put(new Integer(20030402),new Double(1.02415458937198));
		ValoresFixos.put(new Integer(20030403),new Double(1.01128706199461));
		ValoresFixos.put(new Integer(20030404),new Double(1.00499750124938));
		ValoresFixos.put(new Integer(20030407),new Double(1.00580142549312));
		ValoresFixos.put(new Integer(20030408),new Double(0.970583388266315));
		ValoresFixos.put(new Integer(20030409),new Double(0.998302062993463));
		ValoresFixos.put(new Integer(20030410),new Double(0.985713070839357));
		ValoresFixos.put(new Integer(20030411),new Double(1.01104305064274));
		ValoresFixos.put(new Integer(20030414),new Double(1.0132263845038));
		ValoresFixos.put(new Integer(20030415),new Double(1.01953848745157));
		ValoresFixos.put(new Integer(20030416),new Double(0.994795968941021));
		ValoresFixos.put(new Integer(20030417),new Double(1.02922859752553));
		ValoresFixos.put(new Integer(20030422),new Double(1.00459862847923));
		ValoresFixos.put(new Integer(20030423),new Double(0.995342113716672));
		ValoresFixos.put(new Integer(20030424),new Double(0.977892528642892));
		ValoresFixos.put(new Integer(20030425),new Double(1.00049504950495));
		ValoresFixos.put(new Integer(20030428),new Double(1.02770905492331));
		ValoresFixos.put(new Integer(20030429),new Double(1.0173326913818));
		ValoresFixos.put(new Integer(20030430),new Double(0.990455907871904));
		ValoresFixos.put(new Integer(20030505),new Double(1.02197977223859));
		ValoresFixos.put(new Integer(20030507),new Double(1.00958466453674));
		ValoresFixos.put(new Integer(20030508),new Double(0.997298548934856));
		ValoresFixos.put(new Integer(20030509),new Double(1.0226762634471));
		ValoresFixos.put(new Integer(20030512),new Double(1.00802179506584));
		ValoresFixos.put(new Integer(20030513),new Double(1.00758258258258));
		ValoresFixos.put(new Integer(20030514),new Double(1.00283138365248));
		ValoresFixos.put(new Integer(20030515),new Double(0.975555390445055));
		ValoresFixos.put(new Integer(20030516),new Double(1.00723533891851));
		ValoresFixos.put(new Integer(20030519),new Double(0.963780718336484));
		ValoresFixos.put(new Integer(20030520),new Double(0.999921544013808));
		ValoresFixos.put(new Integer(20030521),new Double(1.02267555904276));
		ValoresFixos.put(new Integer(20030522),new Double(1.00514040202547));
		ValoresFixos.put(new Integer(20030523),new Double(1.00320586214793));
		ValoresFixos.put(new Integer(20030526),new Double(0.97785893631591));
		ValoresFixos.put(new Integer(20030527),new Double(1.0306567071273));
		ValoresFixos.put(new Integer(20030528),new Double(1.00362373546731));
		ValoresFixos.put(new Integer(20030529),new Double(1.00834963141267));
		ValoresFixos.put(new Integer(20030530),new Double(1.00126818351361));
		ValoresFixos.put(new Integer(20030602),new Double(0.985620622857994));
		ValoresFixos.put(new Integer(20030603),new Double(1.00914657192532));
		ValoresFixos.put(new Integer(20030604),new Double(1.02756554307116));
		ValoresFixos.put(new Integer(20030605),new Double(1.00451960927249));
		ValoresFixos.put(new Integer(20030606),new Double(1.01037735849057));
		ValoresFixos.put(new Integer(20030609),new Double(0.994469582704877));
		ValoresFixos.put(new Integer(20030610),new Double(1.00346670518561));
		ValoresFixos.put(new Integer(20030611),new Double(0.998776450266302));
		ValoresFixos.put(new Integer(20030612),new Double(1.00763853858903));
		ValoresFixos.put(new Integer(20030613),new Double(0.982192662518773));
		ValoresFixos.put(new Integer(20030616),new Double(1.00720838794233));
		ValoresFixos.put(new Integer(20030617),new Double(0.995951709679751));
		ValoresFixos.put(new Integer(20030618),new Double(0.980692458445235));
		ValoresFixos.put(new Integer(20030620),new Double(0.97187476870698));
		ValoresFixos.put(new Integer(20030623),new Double(0.989338207295712));
		ValoresFixos.put(new Integer(20030624),new Double(1.00900623508583));
		ValoresFixos.put(new Integer(20030625),new Double(0.993744278303326));
		ValoresFixos.put(new Integer(20030626),new Double(1.00660218025487));
		ValoresFixos.put(new Integer(20030627),new Double(0.993288590604027));
		ValoresFixos.put(new Integer(20030630),new Double(0.996084152334152));
		ValoresFixos.put(new Integer(20030701),new Double(1.0245124489324));
		ValoresFixos.put(new Integer(20030702),new Double(1.00142953878564));
		ValoresFixos.put(new Integer(20030703),new Double(0.986776859504132));
		ValoresFixos.put(new Integer(20030704),new Double(1.01119232526268));
		ValoresFixos.put(new Integer(20030707),new Double(1.00911075973195));
		ValoresFixos.put(new Integer(20030708),new Double(1.01619161319206));
		ValoresFixos.put(new Integer(20030710),new Double(0.991335634040678));
		ValoresFixos.put(new Integer(20030711),new Double(0.986667654247834));
		ValoresFixos.put(new Integer(20030714),new Double(1.02004354027475));
		ValoresFixos.put(new Integer(20030715),new Double(1.00191345304681));
		ValoresFixos.put(new Integer(20030716),new Double(0.990671367709711));
		ValoresFixos.put(new Integer(20030717),new Double(1.01000963891154));
		ValoresFixos.put(new Integer(20030718),new Double(1.01262663338717));
		ValoresFixos.put(new Integer(20030721),new Double(0.991445556038857));
		ValoresFixos.put(new Integer(20030722),new Double(1.01162620649313));
		ValoresFixos.put(new Integer(20030723),new Double(0.997397903867004));
		ValoresFixos.put(new Integer(20030724),new Double(0.997246177259222));
		ValoresFixos.put(new Integer(20030725),new Double(0.999200639488409));
		ValoresFixos.put(new Integer(20030728),new Double(0.992218181818182));
		ValoresFixos.put(new Integer(20030729),new Double(0.998534046763908));
		ValoresFixos.put(new Integer(20030730),new Double(0.989136019966234));
		ValoresFixos.put(new Integer(20030731),new Double(1.00719851576994));
		ValoresFixos.put(new Integer(20030801),new Double(0.967432950191571));
		ValoresFixos.put(new Integer(20030804),new Double(0.985453160700685));
		ValoresFixos.put(new Integer(20030805),new Double(1.00927428703918));
		ValoresFixos.put(new Integer(20030806),new Double(0.986905582356995));
		ValoresFixos.put(new Integer(20030807),new Double(1.03414028553693));
		ValoresFixos.put(new Integer(20030808),new Double(1.01290516206483));
		ValoresFixos.put(new Integer(20030811),new Double(1.00444444444444));
		ValoresFixos.put(new Integer(20030812),new Double(1.00309734513274));
		ValoresFixos.put(new Integer(20030813),new Double(1.00588148801647));
		ValoresFixos.put(new Integer(20030814),new Double(1.00950153486332));
		ValoresFixos.put(new Integer(20030815),new Double(1.00564726324935));
		ValoresFixos.put(new Integer(20030818),new Double(1.01843052555796));
		ValoresFixos.put(new Integer(20030819),new Double(1.00084829633819));
		ValoresFixos.put(new Integer(20030820),new Double(1.02182511654188));
		ValoresFixos.put(new Integer(20030821),new Double(1.01403193474805));
		ValoresFixos.put(new Integer(20030822),new Double(0.996114519427403));
		ValoresFixos.put(new Integer(20030825),new Double(0.990419489495655));
		ValoresFixos.put(new Integer(20030826),new Double(1.02798314102121));
		ValoresFixos.put(new Integer(20030827),new Double(1.01781153380831));
		ValoresFixos.put(new Integer(20030828),new Double(0.994849105197121));
		ValoresFixos.put(new Integer(20030829),new Double(1.00723531364089));
    }


    public NeuralNetworkAgent(){
		   TrainingSet=new Vector();
		   Pregao=new Vector();
		   ValoresFixos=new HashMap();
		   PopularValoresFixos();
    }

	static {
		System.out.println("Carregado:NeuralNetworkAgent");		
	}	
	
	public void Initialize() throws AgentException{
		Class[] ClassesToRegister;
		
		ClassesToRegister=new Class[1];		
		try {		
			ClassesToRegister[0]=Class.forName("org.abia.Agents.NeuralNetworkAgent.Prediction");
			Blackboard.getBlackboard().RegisterAgentData(ClassesToRegister);
			Blackboard.getBlackboard().RegisterAgent(this);			
		}catch(Exception e){
			throw (new AgentException());
		}		
	}
	
	public String getAgentNameInBlackboard(){
		return "NeuralNetworkAgent";		
	}
	
	protected void CriarRedeNeural(){
		Double            MaxValue,MinValue;
		Double            MaxValueM,MinValueM;		
		Double            MaxValueRSI,MinValueRSI;
		double            max=-1,min=-1;
		double            ValorSaida[][];
		int               f;
		TrainingData      currentTraining,newTraining;
		AmostraRedeNeural Amostras[];
		
		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			if(max==-1){
				max=currentTraining.getMaxValue().doubleValue();				
			}else{
				if(currentTraining.getMaxValue().doubleValue()>max)
				   max=currentTraining.getMaxValue().doubleValue();
			}			
		}

		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			if(min==-1){
				min=currentTraining.getMaxValue().doubleValue();				
			}else{
				if(currentTraining.getMaxValue().doubleValue()<min)
				   min=currentTraining.getMaxValue().doubleValue();
			}			
		}
		
		MaxValue=new Double(max);
		MinValue=new Double(min);

        max=-1;
		min=-1;
		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			if(max==-1){
				max=currentTraining.getValorRSI().doubleValue();				
			}else{
				if(currentTraining.getValorRSI().doubleValue()>max)
				   max=currentTraining.getValorRSI().doubleValue();
			}			
		}

		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			if(min==-1){
				min=currentTraining.getValorRSI().doubleValue();				
			}else{
				if(currentTraining.getValorRSI().doubleValue()<min)
				   min=currentTraining.getValorRSI().doubleValue();
			}			
		}
		
		MaxValueRSI=new Double(max);
		MinValueRSI=new Double(min);
		
		max=-1;
		min=-1;
		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			if(max==-1){
				max=currentTraining.getValorM().doubleValue();				
			}else{
				if(currentTraining.getValorM().doubleValue()>max)
				   max=currentTraining.getValorM().doubleValue();
			}			
		}

		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			if(min==-1){
				min=currentTraining.getValorM().doubleValue();				
			}else{
				if(currentTraining.getValorM().doubleValue()<min)
				   min=currentTraining.getValorM().doubleValue();
			}			
		}
		
		MaxValueM=new Double(max);
		MinValueM=new Double(min);

		System.out.println("MinValue:"+MinValue+" MaxValue:"+MaxValue+
		                   "MinValueM:"+MinValueM+" MaxValue:"+MaxValueM+
		                   "MinValueRSI:"+MinValueRSI+" MaxValue:"+MaxValueRSI);
		
		ProcessedTrainingSet=new Vector();
		for(f=0;f<TrainingSet.size();f++){
			currentTraining=(TrainingData)TrainingSet.get(f);
			newTraining=new TrainingData();
			newTraining.setValorIt(Normalizar(MaxValue,MinValue,currentTraining.getValorIt()));
			newTraining.setValorM(Normalizar(MaxValueM,MinValueM,currentTraining.getValorM()));
			newTraining.setValorMA10(Normalizar(MaxValue,MinValue,currentTraining.getValorMA10()));
			newTraining.setValorMA5(Normalizar(MaxValue,MinValue,currentTraining.getValorMA5()));
			newTraining.setValorSaida(Normalizar(MaxValue,MinValue,currentTraining.getValorSaida()));
			newTraining.setValorRSI(Normalizar(MaxValueRSI,MinValueRSI,currentTraining.getValorRSI()));			
			ProcessedTrainingSet.add(newTraining);
		}

		for(f=0;f<ProcessedTrainingSet.size();f++){
			currentTraining=(TrainingData)ProcessedTrainingSet.get(f);
			if(min==-1){
				min=currentTraining.getMinNormalizedValorM().doubleValue();				
			}else{
				if(currentTraining.getMinNormalizedValorM().doubleValue()<min)
				   min=currentTraining.getMinNormalizedValorM().doubleValue();
			}			
		}
        MinValue=new Double(min);
        
        min=-1;
		for(f=0;f<ProcessedTrainingSet.size();f++){
			currentTraining=(TrainingData)ProcessedTrainingSet.get(f);
			if(min==-1){
				min=currentTraining.getValorM().doubleValue();				
			}else{
				if(currentTraining.getValorM().doubleValue()<min)
				   min=currentTraining.getValorM().doubleValue();
			}			
		}
		MinValueM=new Double(min);		
		
		min=-1;
		for(f=0;f<ProcessedTrainingSet.size();f++){
			currentTraining=(TrainingData)ProcessedTrainingSet.get(f);
			if(min==-1){
				min=currentTraining.getValorRSI().doubleValue();				
			}else{
				if(currentTraining.getValorRSI().doubleValue()<min)
				   min=currentTraining.getValorRSI().doubleValue();
			}			
		}
		MinValueRSI=new Double(min);		
		
		
		System.out.println("Min em cotacoes:"+MinValue+" "+
                      	   "Min em M:"+MinValueM+" "+"Min Value em RSI:"+MinValueRSI);


		theNeuralNetwork=new RedeNeural(5,5,6,1,ProcessedTrainingSet.size());
		Amostras=new AmostraRedeNeural[ProcessedTrainingSet.size()];
		ValorSaida=new double[1][ProcessedTrainingSet.size()];
		for(f=0;f<Amostras.length;f++){
			currentTraining=(TrainingData)ProcessedTrainingSet.get(f);
			Amostras[f]=new AmostraRedeNeural(5,1,theNeuralNetwork);
			Amostras[f].Valor[0][0]=new Double(currentTraining.getValorIt().doubleValue()+2);
			Amostras[f].Valor[1][0]=new Double(currentTraining.getValorM().doubleValue()+2);
			Amostras[f].Valor[2][0]=new Double(currentTraining.getValorMA10().doubleValue()+2);
			Amostras[f].Valor[3][0]=new Double(currentTraining.getValorMA5().doubleValue()+2);
			Amostras[f].Valor[4][0]=new Double(currentTraining.getValorRSI().doubleValue()+2);
			ValorSaida[0][f]=currentTraining.getValorSaida().doubleValue()+2;
			System.out.println(""+Amostras[f].Valor[0][0]+";"+
			                                     //Amostras[f].Valor[1][0]+";"+
							                     Amostras[f].Valor[2][0]+";"+
								                 Amostras[f].Valor[3][0]+";"+			                                 
											     Amostras[f].Valor[4][0]+";"+
											     ValorSaida[0][f]);			                                 			
		}
		theNeuralNetwork.Carregar(Amostras,ValorSaida);
		theNeuralNetwork.Aprender();
	}
	
	protected void AplicarNovoPadrao(AgentData newPregao){
		
		
	}
	
	protected Double Normalizar(Double Max,Double Min,Double ValorX){
		Double newValor;
		
		newValor=new Double(
		             (ValorX.doubleValue()*2-(Max.doubleValue()+Min.doubleValue()))/
		             (Max.doubleValue()-Min.doubleValue())
		             );
		
		return ValorX;
		 
	}
	
	protected void AtualizarDataSet(AgentData PregaoToProcess){
		Pregao        currentPregao;
		TrainingData  newTS,currentTrainingData;
		AgentData     Indices[]=null;
		QueryCondition[]    Filters;
		AgentData     
		              dataRSI[]=null,
		              dataMA5[]=null,
		              dataMA10[]=null,
		              dataIt[]=null,
		              dataM[]=null;

		int           f;
		
		if(Pregao.size()<PeriodoMinimo) return;
		
		newTS=new TrainingData();
		
		try{
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
		}catch(Exception e){
			System.out.println(e);			
		}
		
		currentPregao=(Pregao)PregaoToProcess;
		for(f=0;f<Indices.length;f++){
		    try{
		    	Filters=new QueryCondition[2];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPregao.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="indice";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=Indices[0].getTimestamp();
				dataRSI=Blackboard.getBlackboard().Query(RSI.class,Filters);		    	
		    	
				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPregao.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="indice";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=Indices[0].getTimestamp();
				Filters[2]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[2]).FieldName="periodo";
				((PostgreSQLFilterCondition)Filters[2]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[2]).ValueName=new Integer(5);
				dataMA5=Blackboard.getBlackboard().Query(MovingAverage.class,Filters);

				Filters=new QueryCondition[3];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPregao.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="indice";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=Indices[0].getTimestamp();
				Filters[2]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[2]).FieldName="periodo";
				((PostgreSQLFilterCondition)Filters[2]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[2]).ValueName=new Integer(10);
				dataMA10=Blackboard.getBlackboard().Query(MovingAverage.class,Filters);		    	

				Filters=new QueryCondition[2];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPregao.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="indice";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=Indices[0].getTimestamp();
				dataIt=Blackboard.getBlackboard().Query(CotacaoIndice.class,Filters);		    	

				Filters=new QueryCondition[2];
				Filters[0]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[0]).FieldName="pregao";
				((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[0]).ValueName=currentPregao.getTimestamp();
				Filters[1]=new PostgreSQLFilterCondition();
				((PostgreSQLFilterCondition)Filters[1]).FieldName="indice";
				((PostgreSQLFilterCondition)Filters[1]).Operator =PostgreSQLQueryCondition.EQUALS;
				((PostgreSQLFilterCondition)Filters[1]).ValueName=Indices[0].getTimestamp();
				dataM=Blackboard.getBlackboard().Query(Momentum.class,Filters);		    	

                newTS.setIndice(Indices[f]);
				newTS.setPregao(currentPregao);
				newTS.setValorIt  (((CotacaoIndice)dataIt[0]).getFechamento());
				newTS.setValorM   (((Momentum)dataM[0]).getValor());
				newTS.setValorMA10(((MovingAverage)dataMA10[0]).getValor());
				newTS.setValorMA5 (((MovingAverage)dataMA5[0]).getValor());
				newTS.setValorRSI (((RSI)dataRSI[0]).getValor());
				newTS.setValorSaida (newTS.getValorIt());
				if(TrainingSet.size()>1){
				   currentTrainingData=(TrainingData)TrainingSet.get(TrainingSet.size()-1);
				   currentTrainingData.setValorSaida(newTS.getValorIt());
				}else{
				   newTS.setValorSaida(newTS.getValorIt());
				}
				TrainingSet.add(newTS);
		    }catch(Exception e){
		    	if(e instanceof java.lang.ArrayIndexOutOfBoundsException){
					f--;
					
					try{
						Thread.sleep(500);
					}catch(Exception x){
						
					}
					
					continue;		    				    	
		    	}else{
					System.out.println(e);
		    	}
		    }		    
		}
	}
	
	public void run(){
		AgentData                     Pregoes[]=null,Indices[]=null;
		QueryCondition[]              Filters;		
		int                           f;		

		while(true){
			try{
				Pregoes=Blackboard.getBlackboard().Query(Pregao.class,null);
				if(Pregoes.length>0) break;
				Thread.sleep(1000);
			}catch(Exception e){
				System.out.println(e);
			}
		}

		try{
			Indices=Blackboard.getBlackboard().Query(Indice.class,null);
			if(Indices.length==0) return;
		}catch(Exception e){
			System.out.println(e);			
		}
		
		while(true){
			if(LastPregao==null){
				try{			
					Pregoes=Blackboard.getBlackboard().Query(Pregao.class,null);
					if(Pregoes.length==0){
						Thread.sleep(1000);
						continue;
					}									   	  
				}catch(Exception e){
					System.out.println(e);					
				}
			}else{
				try{										
					Filters=new QueryCondition[2];
					Filters[0]=new PostgreSQLFilterCondition();
					((PostgreSQLFilterCondition)Filters[0]).FieldName="timestamp";
					((PostgreSQLFilterCondition)Filters[0]).Operator =PostgreSQLQueryCondition.GREATER;
					((PostgreSQLFilterCondition)Filters[0]).ValueName=LastPregao.getTimestamp();
					Filters[1]= new PostgreSQLOrderCondition();
					((PostgreSQLOrderCondition)Filters[1]).Field="timestampval";					
					((PostgreSQLOrderCondition)Filters[1]).Order=OrderCondition.ASC;					
					Pregoes=Blackboard.getBlackboard().Query(Pregao.class,Filters);
					if(Pregoes.length==0){
						Thread.sleep(1000);
						continue;
					}
				}catch(Exception e){
					System.out.println(e);
				}				
			}
			for(f=0;f<Pregoes.length;f++){
				Pregao.add(Pregoes[f]);
				LastPregao=(Pregao)Pregoes[f];
				if(Simulado){
					Prediction   TempPrediction;
					AgentData    PredictionToSave[];
					Double       TempValor;
					int          IndiceTemp;
					
					PredictionToSave=new AgentData[1];					
					TempPrediction=new Prediction();
					TempPrediction.setIndice(Indices[0]);
					TempPrediction.setPregao(Pregoes[f]);
					IndiceTemp=(((Pregao)Pregoes[f]).getData().getYear()+1900)*10000+(((Pregao)Pregoes[f]).getData().getMonth()+1)*100+((Pregao)Pregoes[f]).getData().getDate();
					TempValor=(Double)ValoresFixos.get(new Integer(IndiceTemp));
					if(TempValor==null) continue;
					TempPrediction.setValor(TempValor);
					PredictionToSave[0]=TempPrediction;
					try{
					    Blackboard.getBlackboard().Store(PredictionToSave);					   
					}catch(Exception e){
						System.out.println(e);
						e.printStackTrace();
					}
				}else{
					AtualizarDataSet(LastPregao);
					if(TrainingSet.size()>TrainingSetMinimalSize){
						if(ProcessedTrainingSet==null){
							CriarRedeNeural();						
						}else{
							if((((double)TrainingSet.size()) /
						    	((double)TrainingSetMinimalSize))>1.20){
							 	CriarRedeNeural();						   	
						   	}
						}
						AplicarNovoPadrao(LastPregao);
					}				
				}
			}
		}		
	}
	
	public void run2(){
		AmostraRedeNeural Amostras[],AmostraTeste;
		int f,g;
		double SaidaEsperada[][];
		
		theNeuralNetwork=new RedeNeural(11*8,5,10,5,5);
		
		Amostras=new AmostraRedeNeural[5];
		
		Amostras[0]=new AmostraRedeNeural(11,8,theNeuralNetwork);
		Amostras[1]=new AmostraRedeNeural(11,8,theNeuralNetwork);
		Amostras[2]=new AmostraRedeNeural(11,8,theNeuralNetwork);
		Amostras[3]=new AmostraRedeNeural(11,8,theNeuralNetwork);
		Amostras[4]=new AmostraRedeNeural(11,8,theNeuralNetwork);
		AmostraTeste=new AmostraRedeNeural(11,8,theNeuralNetwork);
						
/*		Amostras[0].Valor=new Double[11][8];
		Amostras[1].Valor=new Double[11][8];
		Amostras[2].Valor=new Double[11][8];
		Amostras[3].Valor=new Double[11][8];
		Amostras[4].Valor=new Double[11][8];*/		
		
/*		Amostras[0].Valor [0][0]=new Double(0);
		Amostras[0].Valor [1][0]=new Double(0);
		Amostras[0].Valor [2][0]=new Double(0);
		Amostras[0].Valor [3][0]=new Double(0);
		Amostras[0].Valor [4][0]=new Double(0);
		Amostras[0].Valor [5][0]=new Double(0);
		Amostras[0].Valor [6][0]=new Double(0);
		Amostras[0].Valor [7][0]=new Double(0);
		Amostras[0].Valor [8][0]=new Double(0);
		Amostras[0].Valor [9][0]=new Double(0);
		Amostras[0].Valor[10][0]=new Double(0);

		Amostras[0].Valor [0][1]=new Double(0);
		Amostras[0].Valor [1][1]=new Double(0);
		Amostras[0].Valor [2][1]=new Double(1);
		Amostras[0].Valor [3][1]=new Double(1);
		Amostras[0].Valor [4][1]=new Double(1);
		Amostras[0].Valor [5][1]=new Double(1);
		Amostras[0].Valor [6][1]=new Double(1);
		Amostras[0].Valor [7][1]=new Double(1);
		Amostras[0].Valor [8][1]=new Double(1);
		Amostras[0].Valor [9][1]=new Double(0);
		Amostras[0].Valor[10][1]=new Double(0);		
		
		Amostras[0].Valor [0][2]=new Double(0);
		Amostras[0].Valor [1][2]=new Double(0);
		Amostras[0].Valor [2][2]=new Double(1);
		Amostras[0].Valor [3][2]=new Double(1);
		Amostras[0].Valor [4][2]=new Double(0);
		Amostras[0].Valor [5][2]=new Double(0);
		Amostras[0].Valor [6][2]=new Double(0);
		Amostras[0].Valor [7][2]=new Double(1);
		Amostras[0].Valor [8][2]=new Double(1);
		Amostras[0].Valor [9][2]=new Double(0);
		Amostras[0].Valor[10][2]=new Double(0);

		Amostras[0].Valor [0][3]=new Double(0);
		Amostras[0].Valor [1][3]=new Double(0);
		Amostras[0].Valor [2][3]=new Double(1);
		Amostras[0].Valor [3][3]=new Double(1);
		Amostras[0].Valor [4][3]=new Double(0);
		Amostras[0].Valor [5][3]=new Double(0);
		Amostras[0].Valor [6][3]=new Double(0);
		Amostras[0].Valor [7][3]=new Double(1);
		Amostras[0].Valor [8][3]=new Double(1);
		Amostras[0].Valor [9][3]=new Double(0);
		Amostras[0].Valor[10][3]=new Double(0);
		
		Amostras[0].Valor [0][4]=new Double(0);
		Amostras[0].Valor [1][4]=new Double(0);
		Amostras[0].Valor [2][4]=new Double(1);
		Amostras[0].Valor [3][4]=new Double(1);
		Amostras[0].Valor [4][4]=new Double(0);
		Amostras[0].Valor [5][4]=new Double(0);
		Amostras[0].Valor [6][4]=new Double(0);
		Amostras[0].Valor [7][4]=new Double(1);
		Amostras[0].Valor [8][4]=new Double(1);
		Amostras[0].Valor [9][4]=new Double(0);
		Amostras[0].Valor[10][4]=new Double(0);

		Amostras[0].Valor [0][5]=new Double(0);
		Amostras[0].Valor [1][5]=new Double(0);
		Amostras[0].Valor [2][5]=new Double(1);
		Amostras[0].Valor [3][5]=new Double(1);
		Amostras[0].Valor [4][5]=new Double(0);
		Amostras[0].Valor [5][5]=new Double(0);
		Amostras[0].Valor [6][5]=new Double(0);
		Amostras[0].Valor [7][5]=new Double(1);
		Amostras[0].Valor [8][5]=new Double(1);
		Amostras[0].Valor [9][5]=new Double(0);
		Amostras[0].Valor[10][5]=new Double(0);		
		
		Amostras[0].Valor [0][6]=new Double(0);
		Amostras[0].Valor [1][6]=new Double(0);
		Amostras[0].Valor [2][6]=new Double(1);
		Amostras[0].Valor [3][6]=new Double(1);
		Amostras[0].Valor [4][6]=new Double(1);
		Amostras[0].Valor [5][6]=new Double(1);
		Amostras[0].Valor [6][6]=new Double(1);
		Amostras[0].Valor [7][6]=new Double(1);
		Amostras[0].Valor [8][6]=new Double(1);
		Amostras[0].Valor [9][6]=new Double(0);
		Amostras[0].Valor[10][6]=new Double(0);

		Amostras[0].Valor [0][7]=new Double(0);
		Amostras[0].Valor [1][7]=new Double(0);
		Amostras[0].Valor [2][7]=new Double(0);
		Amostras[0].Valor [3][7]=new Double(0);
		Amostras[0].Valor [4][7]=new Double(0);
		Amostras[0].Valor [5][7]=new Double(0);
		Amostras[0].Valor [6][7]=new Double(0);
		Amostras[0].Valor [7][7]=new Double(0);
		Amostras[0].Valor [8][7]=new Double(0);
		Amostras[0].Valor [9][7]=new Double(0);
		Amostras[0].Valor[10][7]=new Double(0);

		Amostras[1].Valor [0][0]=new Double(0);
		Amostras[1].Valor [1][0]=new Double(0);
		Amostras[1].Valor [2][0]=new Double(0);
		Amostras[1].Valor [3][0]=new Double(0);
		Amostras[1].Valor [4][0]=new Double(0);
		Amostras[1].Valor [5][0]=new Double(0);
		Amostras[1].Valor [6][0]=new Double(0);
		Amostras[1].Valor [7][0]=new Double(0);
		Amostras[1].Valor [8][0]=new Double(0);
		Amostras[1].Valor [9][0]=new Double(0);
		Amostras[1].Valor[10][0]=new Double(0);

		Amostras[1].Valor [0][1]=new Double(0);
		Amostras[1].Valor [1][1]=new Double(0);
		Amostras[1].Valor [2][1]=new Double(0);
		Amostras[1].Valor [3][1]=new Double(0);
		Amostras[1].Valor [4][1]=new Double(0);
		Amostras[1].Valor [5][1]=new Double(0);
		Amostras[1].Valor [6][1]=new Double(1);
		Amostras[1].Valor [7][1]=new Double(1);
		Amostras[1].Valor [8][1]=new Double(0);
		Amostras[1].Valor [9][1]=new Double(0);
		Amostras[1].Valor[10][1]=new Double(0);		
		
		Amostras[1].Valor [0][2]=new Double(0);
		Amostras[1].Valor [1][2]=new Double(0);
		Amostras[1].Valor [2][2]=new Double(0);
		Amostras[1].Valor [3][2]=new Double(0);
		Amostras[1].Valor [4][2]=new Double(1);
		Amostras[1].Valor [5][2]=new Double(1);
		Amostras[1].Valor [6][2]=new Double(1);
		Amostras[1].Valor [7][2]=new Double(1);
		Amostras[1].Valor [8][2]=new Double(0);
		Amostras[1].Valor [9][2]=new Double(0);
		Amostras[1].Valor[10][2]=new Double(0);

		Amostras[1].Valor [0][3]=new Double(0);
		Amostras[1].Valor [1][3]=new Double(0);
		Amostras[1].Valor [2][3]=new Double(1);
		Amostras[1].Valor [3][3]=new Double(1);
		Amostras[1].Valor [4][3]=new Double(1);
		Amostras[1].Valor [5][3]=new Double(1);
		Amostras[1].Valor [6][3]=new Double(1);
		Amostras[1].Valor [7][3]=new Double(1);
		Amostras[1].Valor [8][3]=new Double(0);
		Amostras[1].Valor [9][3]=new Double(0);
		Amostras[1].Valor[10][3]=new Double(0);
		
		Amostras[1].Valor [0][4]=new Double(0);
		Amostras[1].Valor [1][4]=new Double(0);
		Amostras[1].Valor [2][4]=new Double(0);
		Amostras[1].Valor [3][4]=new Double(0);
		Amostras[1].Valor [4][4]=new Double(0);
		Amostras[1].Valor [5][4]=new Double(1);
		Amostras[1].Valor [6][4]=new Double(1);
		Amostras[1].Valor [7][4]=new Double(1);
		Amostras[1].Valor [8][4]=new Double(0);
		Amostras[1].Valor [9][4]=new Double(0);
		Amostras[1].Valor[10][4]=new Double(0);

		Amostras[1].Valor [0][5]=new Double(0);
		Amostras[1].Valor [1][5]=new Double(0);
		Amostras[1].Valor [2][5]=new Double(0);
		Amostras[1].Valor [3][5]=new Double(0);
		Amostras[1].Valor [4][5]=new Double(0);
		Amostras[1].Valor [5][5]=new Double(1);
		Amostras[1].Valor [6][5]=new Double(1);
		Amostras[1].Valor [7][5]=new Double(1);
		Amostras[1].Valor [8][5]=new Double(0);
		Amostras[1].Valor [9][5]=new Double(0);
		Amostras[1].Valor[10][5]=new Double(0);		
		
		Amostras[1].Valor [0][6]=new Double(0);
		Amostras[1].Valor [1][6]=new Double(0);
		Amostras[1].Valor [2][6]=new Double(0);
		Amostras[1].Valor [3][6]=new Double(0);
		Amostras[1].Valor [4][6]=new Double(0);
		Amostras[1].Valor [5][6]=new Double(1);
		Amostras[1].Valor [6][6]=new Double(1);
		Amostras[1].Valor [7][6]=new Double(1);
		Amostras[1].Valor [8][6]=new Double(0);
		Amostras[1].Valor [9][6]=new Double(0);
		Amostras[1].Valor[10][6]=new Double(0);

		Amostras[1].Valor [0][7]=new Double(0);
		Amostras[1].Valor [1][7]=new Double(0);
		Amostras[1].Valor [2][7]=new Double(0);
		Amostras[1].Valor [3][7]=new Double(0);
		Amostras[1].Valor [4][7]=new Double(0);
		Amostras[1].Valor [5][7]=new Double(0);
		Amostras[1].Valor [6][7]=new Double(0);
		Amostras[1].Valor [7][7]=new Double(0);
		Amostras[1].Valor [8][7]=new Double(0);
		Amostras[1].Valor [9][7]=new Double(0);
		Amostras[1].Valor[10][7]=new Double(0);

		Amostras[2].Valor [0][0]=new Double(0);
		Amostras[2].Valor [1][0]=new Double(0);
		Amostras[2].Valor [2][0]=new Double(0);
		Amostras[2].Valor [3][0]=new Double(0);
		Amostras[2].Valor [4][0]=new Double(0);
		Amostras[2].Valor [5][0]=new Double(0);
		Amostras[2].Valor [6][0]=new Double(0);
		Amostras[2].Valor [7][0]=new Double(0);
		Amostras[2].Valor [8][0]=new Double(0);
		Amostras[2].Valor [9][0]=new Double(0);
		Amostras[2].Valor[10][0]=new Double(0);

		Amostras[2].Valor [0][1]=new Double(0);
		Amostras[2].Valor [1][1]=new Double(0);
		Amostras[2].Valor [2][1]=new Double(1);
		Amostras[2].Valor [3][1]=new Double(1);
		Amostras[2].Valor [4][1]=new Double(1);
		Amostras[2].Valor [5][1]=new Double(1);
		Amostras[2].Valor [6][1]=new Double(1);
		Amostras[2].Valor [7][1]=new Double(1);
		Amostras[2].Valor [8][1]=new Double(1);
		Amostras[2].Valor [9][1]=new Double(0);
		Amostras[2].Valor[10][1]=new Double(0);		
		
		Amostras[2].Valor [0][2]=new Double(0);
		Amostras[2].Valor [1][2]=new Double(0);
		Amostras[2].Valor [2][2]=new Double(1);
		Amostras[2].Valor [3][2]=new Double(1);
		Amostras[2].Valor [4][2]=new Double(1);
		Amostras[2].Valor [5][2]=new Double(1);
		Amostras[2].Valor [6][2]=new Double(1);
		Amostras[2].Valor [7][2]=new Double(1);
		Amostras[2].Valor [8][2]=new Double(1);
		Amostras[2].Valor [9][2]=new Double(0);
		Amostras[2].Valor[10][2]=new Double(0);

		Amostras[2].Valor [0][3]=new Double(0);
		Amostras[2].Valor [1][3]=new Double(0);
		Amostras[2].Valor [2][3]=new Double(0);
		Amostras[2].Valor [3][3]=new Double(0);
		Amostras[2].Valor [4][3]=new Double(0);
		Amostras[2].Valor [5][3]=new Double(0);
		Amostras[2].Valor [6][3]=new Double(1);
		Amostras[2].Valor [7][3]=new Double(1);
		Amostras[2].Valor [8][3]=new Double(1);
		Amostras[2].Valor [9][3]=new Double(0);
		Amostras[2].Valor[10][3]=new Double(0);
		
		Amostras[2].Valor [0][4]=new Double(0);
		Amostras[2].Valor [1][4]=new Double(0);
		Amostras[2].Valor [2][4]=new Double(1);
		Amostras[2].Valor [3][4]=new Double(1);
		Amostras[2].Valor [4][4]=new Double(1);
		Amostras[2].Valor [5][4]=new Double(1);
		Amostras[2].Valor [6][4]=new Double(1);
		Amostras[2].Valor [7][4]=new Double(1);
		Amostras[2].Valor [8][4]=new Double(1);
		Amostras[2].Valor [9][4]=new Double(0);
		Amostras[2].Valor[10][4]=new Double(0);

		Amostras[2].Valor [0][5]=new Double(0);
		Amostras[2].Valor [1][5]=new Double(0);
		Amostras[2].Valor [2][5]=new Double(1);
		Amostras[2].Valor [3][5]=new Double(1);
		Amostras[2].Valor [4][5]=new Double(1);
		Amostras[2].Valor [5][5]=new Double(0);
		Amostras[2].Valor [6][5]=new Double(0);
		Amostras[2].Valor [7][5]=new Double(0);
		Amostras[2].Valor [8][5]=new Double(0);
		Amostras[2].Valor [9][5]=new Double(0);
		Amostras[2].Valor[10][5]=new Double(0);		
		
		Amostras[2].Valor [0][6]=new Double(0);
		Amostras[2].Valor [1][6]=new Double(0);
		Amostras[2].Valor [2][6]=new Double(1);
		Amostras[2].Valor [3][6]=new Double(1);
		Amostras[2].Valor [4][6]=new Double(1);
		Amostras[2].Valor [5][6]=new Double(1);
		Amostras[2].Valor [6][6]=new Double(1);
		Amostras[2].Valor [7][6]=new Double(1);
		Amostras[2].Valor [8][6]=new Double(1);
		Amostras[2].Valor [9][6]=new Double(0);
		Amostras[2].Valor[10][6]=new Double(0);

		Amostras[2].Valor [0][7]=new Double(0);
		Amostras[2].Valor [1][7]=new Double(0);
		Amostras[2].Valor [2][7]=new Double(0);
		Amostras[2].Valor [3][7]=new Double(0);
		Amostras[2].Valor [4][7]=new Double(0);
		Amostras[2].Valor [5][7]=new Double(0);
		Amostras[2].Valor [6][7]=new Double(0);
		Amostras[2].Valor [7][7]=new Double(0);
		Amostras[2].Valor [8][7]=new Double(0);
		Amostras[2].Valor [9][7]=new Double(0);
		Amostras[2].Valor[10][7]=new Double(0);
				
		Amostras[3].Valor [0][0]=new Double(0);
		Amostras[3].Valor [1][0]=new Double(0);
		Amostras[3].Valor [2][0]=new Double(0);
		Amostras[3].Valor [3][0]=new Double(0);
		Amostras[3].Valor [4][0]=new Double(0);
		Amostras[3].Valor [5][0]=new Double(0);
		Amostras[3].Valor [6][0]=new Double(0);
		Amostras[3].Valor [7][0]=new Double(0);
		Amostras[3].Valor [8][0]=new Double(0);
		Amostras[3].Valor [9][0]=new Double(0);
		Amostras[3].Valor[10][0]=new Double(0);

		Amostras[3].Valor [0][1]=new Double(0);
		Amostras[3].Valor [1][1]=new Double(0);
		Amostras[3].Valor [2][1]=new Double(1);
		Amostras[3].Valor [3][1]=new Double(1);
		Amostras[3].Valor [4][1]=new Double(1);
		Amostras[3].Valor [5][1]=new Double(1);
		Amostras[3].Valor [6][1]=new Double(1);
		Amostras[3].Valor [7][1]=new Double(1);
		Amostras[3].Valor [8][1]=new Double(1);
		Amostras[3].Valor [9][1]=new Double(0);
		Amostras[3].Valor[10][1]=new Double(0);		
		
		Amostras[3].Valor [0][2]=new Double(0);
		Amostras[3].Valor [1][2]=new Double(0);
		Amostras[3].Valor [2][2]=new Double(0);
		Amostras[3].Valor [3][2]=new Double(0);
		Amostras[3].Valor [4][2]=new Double(0);
		Amostras[3].Valor [5][2]=new Double(0);
		Amostras[3].Valor [6][2]=new Double(1);
		Amostras[3].Valor [7][2]=new Double(1);
		Amostras[3].Valor [8][2]=new Double(1);
		Amostras[3].Valor [9][2]=new Double(0);
		Amostras[3].Valor[10][2]=new Double(0);

		Amostras[3].Valor [0][3]=new Double(0);
		Amostras[3].Valor [1][3]=new Double(0);
		Amostras[3].Valor [2][3]=new Double(0);
		Amostras[3].Valor [3][3]=new Double(0);
		Amostras[3].Valor [4][3]=new Double(1);
		Amostras[3].Valor [5][3]=new Double(1);
		Amostras[3].Valor [6][3]=new Double(1);
		Amostras[3].Valor [7][3]=new Double(1);
		Amostras[3].Valor [8][3]=new Double(1);
		Amostras[3].Valor [9][3]=new Double(0);
		Amostras[3].Valor[10][3]=new Double(0);
		
		Amostras[3].Valor [0][4]=new Double(0);
		Amostras[3].Valor [1][4]=new Double(0);
		Amostras[3].Valor [2][4]=new Double(0);
		Amostras[3].Valor [3][4]=new Double(0);
		Amostras[3].Valor [4][4]=new Double(1);
		Amostras[3].Valor [5][4]=new Double(1);
		Amostras[3].Valor [6][4]=new Double(1);
		Amostras[3].Valor [7][4]=new Double(1);
		Amostras[3].Valor [8][4]=new Double(1);
		Amostras[3].Valor [9][4]=new Double(0);
		Amostras[3].Valor[10][4]=new Double(0);

		Amostras[3].Valor [0][5]=new Double(0);
		Amostras[3].Valor [1][5]=new Double(0);
		Amostras[3].Valor [2][5]=new Double(0);
		Amostras[3].Valor [3][5]=new Double(0);
		Amostras[3].Valor [4][5]=new Double(0);
		Amostras[3].Valor [5][5]=new Double(0);
		Amostras[3].Valor [6][5]=new Double(1);
		Amostras[3].Valor [7][5]=new Double(1);
		Amostras[3].Valor [8][5]=new Double(1);
		Amostras[3].Valor [9][5]=new Double(0);
		Amostras[3].Valor[10][5]=new Double(0);		
		
		Amostras[3].Valor [0][6]=new Double(0);
		Amostras[3].Valor [1][6]=new Double(0);
		Amostras[3].Valor [2][6]=new Double(1);
		Amostras[3].Valor [3][6]=new Double(1);
		Amostras[3].Valor [4][6]=new Double(1);
		Amostras[3].Valor [5][6]=new Double(1);
		Amostras[3].Valor [6][6]=new Double(1);
		Amostras[3].Valor [7][6]=new Double(1);
		Amostras[3].Valor [8][6]=new Double(1);
		Amostras[3].Valor [9][6]=new Double(0);
		Amostras[3].Valor[10][6]=new Double(0);

		Amostras[3].Valor [0][7]=new Double(0);
		Amostras[3].Valor [1][7]=new Double(0);
		Amostras[3].Valor [2][7]=new Double(0);
		Amostras[3].Valor [3][7]=new Double(0);
		Amostras[3].Valor [4][7]=new Double(0);
		Amostras[3].Valor [5][7]=new Double(0);
		Amostras[3].Valor [6][7]=new Double(0);
		Amostras[3].Valor [7][7]=new Double(0);
		Amostras[3].Valor [8][7]=new Double(0);
		Amostras[3].Valor [9][7]=new Double(0);
		Amostras[3].Valor[10][7]=new Double(0);		
		
		Amostras[4].Valor [0][0]=new Double(0);
		Amostras[4].Valor [1][0]=new Double(0);
		Amostras[4].Valor [2][0]=new Double(0);
		Amostras[4].Valor [3][0]=new Double(0);
		Amostras[4].Valor [4][0]=new Double(0);
		Amostras[4].Valor [5][0]=new Double(0);
		Amostras[4].Valor [6][0]=new Double(0);
		Amostras[4].Valor [7][0]=new Double(0);
		Amostras[4].Valor [8][0]=new Double(0);
		Amostras[4].Valor [9][0]=new Double(0);
		Amostras[4].Valor[10][0]=new Double(0);

		Amostras[4].Valor [0][1]=new Double(0);
		Amostras[4].Valor [1][1]=new Double(0);
		Amostras[4].Valor [2][1]=new Double(1);
		Amostras[4].Valor [3][1]=new Double(1);
		Amostras[4].Valor [4][1]=new Double(0);
		Amostras[4].Valor [5][1]=new Double(0);
		Amostras[4].Valor [6][1]=new Double(1);
		Amostras[4].Valor [7][1]=new Double(1);
		Amostras[4].Valor [8][1]=new Double(1);
		Amostras[4].Valor [9][1]=new Double(0);
		Amostras[4].Valor[10][1]=new Double(0);		
		
		Amostras[4].Valor [0][2]=new Double(0);
		Amostras[4].Valor [1][2]=new Double(0);
		Amostras[4].Valor [2][2]=new Double(1);
		Amostras[4].Valor [3][2]=new Double(1);
		Amostras[4].Valor [4][2]=new Double(0);
		Amostras[4].Valor [5][2]=new Double(0);
		Amostras[4].Valor [6][2]=new Double(1);
		Amostras[4].Valor [7][2]=new Double(1);
		Amostras[4].Valor [8][2]=new Double(1);
		Amostras[4].Valor [9][2]=new Double(0);
		Amostras[4].Valor[10][2]=new Double(0);

		Amostras[4].Valor [0][3]=new Double(0);
		Amostras[4].Valor [1][3]=new Double(0);
		Amostras[4].Valor [2][3]=new Double(1);
		Amostras[4].Valor [3][3]=new Double(1);
		Amostras[4].Valor [4][3]=new Double(0);
		Amostras[4].Valor [5][3]=new Double(0);
		Amostras[4].Valor [6][3]=new Double(0);
		Amostras[4].Valor [7][3]=new Double(1);
		Amostras[4].Valor [8][3]=new Double(1);
		Amostras[4].Valor [9][3]=new Double(1);
		Amostras[4].Valor[10][3]=new Double(0);
		
		Amostras[4].Valor [0][4]=new Double(0);
		Amostras[4].Valor [1][4]=new Double(0);
		Amostras[4].Valor [2][4]=new Double(1);
		Amostras[4].Valor [3][4]=new Double(1);
		Amostras[4].Valor [4][4]=new Double(1);
		Amostras[4].Valor [5][4]=new Double(1);
		Amostras[4].Valor [6][4]=new Double(1);
		Amostras[4].Valor [7][4]=new Double(1);
		Amostras[4].Valor [8][4]=new Double(1);
		Amostras[4].Valor [9][4]=new Double(0);
		Amostras[4].Valor[10][4]=new Double(0);

		Amostras[4].Valor [0][5]=new Double(0);
		Amostras[4].Valor [1][5]=new Double(0);
		Amostras[4].Valor [2][5]=new Double(0);
		Amostras[4].Valor [3][5]=new Double(0);
		Amostras[4].Valor [4][5]=new Double(0);
		Amostras[4].Valor [5][5]=new Double(0);
		Amostras[4].Valor [6][5]=new Double(1);
		Amostras[4].Valor [7][5]=new Double(1);
		Amostras[4].Valor [8][5]=new Double(1);
		Amostras[4].Valor [9][5]=new Double(0);
		Amostras[4].Valor[10][5]=new Double(0);		
		
		Amostras[4].Valor [0][6]=new Double(0);
		Amostras[4].Valor [1][6]=new Double(0);
		Amostras[4].Valor [2][6]=new Double(0);
		Amostras[4].Valor [3][6]=new Double(0);
		Amostras[4].Valor [4][6]=new Double(0);
		Amostras[4].Valor [5][6]=new Double(0);
		Amostras[4].Valor [6][6]=new Double(1);
		Amostras[4].Valor [7][6]=new Double(1);
		Amostras[4].Valor [8][6]=new Double(1);
		Amostras[4].Valor [9][6]=new Double(0);
		Amostras[4].Valor[10][6]=new Double(0);

		Amostras[4].Valor [0][7]=new Double(0);
		Amostras[4].Valor [1][7]=new Double(0);
		Amostras[4].Valor [2][7]=new Double(0);
		Amostras[4].Valor [3][7]=new Double(0);
		Amostras[4].Valor [4][7]=new Double(0);
		Amostras[4].Valor [5][7]=new Double(0);
		Amostras[4].Valor [6][7]=new Double(0);
		Amostras[4].Valor [7][7]=new Double(0);
		Amostras[4].Valor [8][7]=new Double(0);
		Amostras[4].Valor [9][7]=new Double(0);
		Amostras[4].Valor[10][7]=new Double(0);		
*/

Amostras[0].Valor[0][0]=new Double(0);
Amostras[0].Valor[1][0]=new Double(0);
Amostras[0].Valor[2][0]=new Double(0);
Amostras[0].Valor[3][0]=new Double(0);
Amostras[0].Valor[4][0]=new Double(0);
Amostras[0].Valor[5][0]=new Double(0);
Amostras[0].Valor[6][0]=new Double(0);
Amostras[0].Valor[7][0]=new Double(0);
Amostras[0].Valor[8][0]=new Double(0);
Amostras[0].Valor[9][0]=new Double(0);
Amostras[0].Valor[10][0]=new Double(0);
Amostras[0].Valor[0][1]=new Double(0);
Amostras[0].Valor[1][1]=new Double(0);
Amostras[0].Valor[2][1]=new Double(1);
Amostras[0].Valor[3][1]=new Double(1);
Amostras[0].Valor[4][1]=new Double(1);
Amostras[0].Valor[5][1]=new Double(1);
Amostras[0].Valor[6][1]=new Double(1);
Amostras[0].Valor[7][1]=new Double(1);
Amostras[0].Valor[8][1]=new Double(1);
Amostras[0].Valor[9][1]=new Double(0);
Amostras[0].Valor[10][1]=new Double(0);
Amostras[0].Valor[0][2]=new Double(0);
Amostras[0].Valor[1][2]=new Double(0);
Amostras[0].Valor[2][2]=new Double(1);
Amostras[0].Valor[3][2]=new Double(1);
Amostras[0].Valor[4][2]=new Double(0);
Amostras[0].Valor[5][2]=new Double(0);
Amostras[0].Valor[6][2]=new Double(0);
Amostras[0].Valor[7][2]=new Double(1);
Amostras[0].Valor[8][2]=new Double(1);
Amostras[0].Valor[9][2]=new Double(0);
Amostras[0].Valor[10][2]=new Double(0);
Amostras[0].Valor[0][3]=new Double(0);
Amostras[0].Valor[1][3]=new Double(0);
Amostras[0].Valor[2][3]=new Double(1);
Amostras[0].Valor[3][3]=new Double(1);
Amostras[0].Valor[4][3]=new Double(0);
Amostras[0].Valor[5][3]=new Double(0);
Amostras[0].Valor[6][3]=new Double(0);
Amostras[0].Valor[7][3]=new Double(1);
Amostras[0].Valor[8][3]=new Double(1);
Amostras[0].Valor[9][3]=new Double(0);
Amostras[0].Valor[10][3]=new Double(0);
Amostras[0].Valor[0][4]=new Double(0);
Amostras[0].Valor[1][4]=new Double(0);
Amostras[0].Valor[2][4]=new Double(1);
Amostras[0].Valor[3][4]=new Double(1);
Amostras[0].Valor[4][4]=new Double(0);
Amostras[0].Valor[5][4]=new Double(0);
Amostras[0].Valor[6][4]=new Double(0);
Amostras[0].Valor[7][4]=new Double(1);
Amostras[0].Valor[8][4]=new Double(1);
Amostras[0].Valor[9][4]=new Double(0);
Amostras[0].Valor[10][4]=new Double(0);
Amostras[0].Valor[0][5]=new Double(0);
Amostras[0].Valor[1][5]=new Double(0);
Amostras[0].Valor[2][5]=new Double(1);
Amostras[0].Valor[3][5]=new Double(1);
Amostras[0].Valor[4][5]=new Double(0);
Amostras[0].Valor[5][5]=new Double(0);
Amostras[0].Valor[6][5]=new Double(0);
Amostras[0].Valor[7][5]=new Double(1);
Amostras[0].Valor[8][5]=new Double(1);
Amostras[0].Valor[9][5]=new Double(0);
Amostras[0].Valor[10][5]=new Double(0);
Amostras[0].Valor[0][6]=new Double(0);
Amostras[0].Valor[1][6]=new Double(0);
Amostras[0].Valor[2][6]=new Double(1);
Amostras[0].Valor[3][6]=new Double(1);
Amostras[0].Valor[4][6]=new Double(1);
Amostras[0].Valor[5][6]=new Double(1);
Amostras[0].Valor[6][6]=new Double(1);
Amostras[0].Valor[7][6]=new Double(1);
Amostras[0].Valor[8][6]=new Double(1);
Amostras[0].Valor[9][6]=new Double(0);
Amostras[0].Valor[10][6]=new Double(0);
Amostras[0].Valor[0][7]=new Double(0);
Amostras[0].Valor[1][7]=new Double(0);
Amostras[0].Valor[2][7]=new Double(0);
Amostras[0].Valor[3][7]=new Double(0);
Amostras[0].Valor[4][7]=new Double(0);
Amostras[0].Valor[5][7]=new Double(0);
Amostras[0].Valor[6][7]=new Double(0);
Amostras[0].Valor[7][7]=new Double(0);
Amostras[0].Valor[8][7]=new Double(0);
Amostras[0].Valor[9][7]=new Double(0);
Amostras[0].Valor[10][7]=new Double(0);
 
Amostras[1].Valor[0][0]=new Double(0);
Amostras[1].Valor[1][0]=new Double(0);
Amostras[1].Valor[2][0]=new Double(0);
Amostras[1].Valor[3][0]=new Double(0);
Amostras[1].Valor[4][0]=new Double(0);
Amostras[1].Valor[5][0]=new Double(0);
Amostras[1].Valor[6][0]=new Double(0);
Amostras[1].Valor[7][0]=new Double(0);
Amostras[1].Valor[8][0]=new Double(0);
Amostras[1].Valor[9][0]=new Double(0);
Amostras[1].Valor[10][0]=new Double(0);
Amostras[1].Valor[0][1]=new Double(0);
Amostras[1].Valor[1][1]=new Double(0);
Amostras[1].Valor[2][1]=new Double(0);
Amostras[1].Valor[3][1]=new Double(0);
Amostras[1].Valor[4][1]=new Double(0);
Amostras[1].Valor[5][1]=new Double(0);
Amostras[1].Valor[6][1]=new Double(1);
Amostras[1].Valor[7][1]=new Double(1);
Amostras[1].Valor[8][1]=new Double(0);
Amostras[1].Valor[9][1]=new Double(0);
Amostras[1].Valor[10][1]=new Double(0);
Amostras[1].Valor[0][2]=new Double(0);
Amostras[1].Valor[1][2]=new Double(0);
Amostras[1].Valor[2][2]=new Double(0);
Amostras[1].Valor[3][2]=new Double(0);
Amostras[1].Valor[4][2]=new Double(1);
Amostras[1].Valor[5][2]=new Double(1);
Amostras[1].Valor[6][2]=new Double(1);
Amostras[1].Valor[7][2]=new Double(1);
Amostras[1].Valor[8][2]=new Double(0);
Amostras[1].Valor[9][2]=new Double(0);
Amostras[1].Valor[10][2]=new Double(0);
Amostras[1].Valor[0][3]=new Double(0);
Amostras[1].Valor[1][3]=new Double(0);
Amostras[1].Valor[2][3]=new Double(1);
Amostras[1].Valor[3][3]=new Double(1);
Amostras[1].Valor[4][3]=new Double(1);
Amostras[1].Valor[5][3]=new Double(1);
Amostras[1].Valor[6][3]=new Double(1);
Amostras[1].Valor[7][3]=new Double(1);
Amostras[1].Valor[8][3]=new Double(0);
Amostras[1].Valor[9][3]=new Double(0);
Amostras[1].Valor[10][3]=new Double(0);
Amostras[1].Valor[0][4]=new Double(0);
Amostras[1].Valor[1][4]=new Double(0);
Amostras[1].Valor[2][4]=new Double(0);
Amostras[1].Valor[3][4]=new Double(0);
Amostras[1].Valor[4][4]=new Double(0);
Amostras[1].Valor[5][4]=new Double(1);
Amostras[1].Valor[6][4]=new Double(1);
Amostras[1].Valor[7][4]=new Double(1);
Amostras[1].Valor[8][4]=new Double(0);
Amostras[1].Valor[9][4]=new Double(0);
Amostras[1].Valor[10][4]=new Double(0);
Amostras[1].Valor[0][5]=new Double(0);
Amostras[1].Valor[1][5]=new Double(0);
Amostras[1].Valor[2][5]=new Double(0);
Amostras[1].Valor[3][5]=new Double(0);
Amostras[1].Valor[4][5]=new Double(0);
Amostras[1].Valor[5][5]=new Double(1);
Amostras[1].Valor[6][5]=new Double(1);
Amostras[1].Valor[7][5]=new Double(1);
Amostras[1].Valor[8][5]=new Double(0);
Amostras[1].Valor[9][5]=new Double(0);
Amostras[1].Valor[10][5]=new Double(0);
Amostras[1].Valor[0][6]=new Double(0);
Amostras[1].Valor[1][6]=new Double(0);
Amostras[1].Valor[2][6]=new Double(0);
Amostras[1].Valor[3][6]=new Double(0);
Amostras[1].Valor[4][6]=new Double(0);
Amostras[1].Valor[5][6]=new Double(1);
Amostras[1].Valor[6][6]=new Double(1);
Amostras[1].Valor[7][6]=new Double(1);
Amostras[1].Valor[8][6]=new Double(0);
Amostras[1].Valor[9][6]=new Double(0);
Amostras[1].Valor[10][6]=new Double(0);
Amostras[1].Valor[0][7]=new Double(0);
Amostras[1].Valor[1][7]=new Double(0);
Amostras[1].Valor[2][7]=new Double(0);
Amostras[1].Valor[3][7]=new Double(0);
Amostras[1].Valor[4][7]=new Double(0);
Amostras[1].Valor[5][7]=new Double(0);
Amostras[1].Valor[6][7]=new Double(0);
Amostras[1].Valor[7][7]=new Double(0);
Amostras[1].Valor[8][7]=new Double(0);
Amostras[1].Valor[9][7]=new Double(0);
Amostras[1].Valor[10][7]=new Double(0);
 
Amostras[2].Valor[0][0]=new Double(0);
Amostras[2].Valor[1][0]=new Double(0);
Amostras[2].Valor[2][0]=new Double(0);
Amostras[2].Valor[3][0]=new Double(0);
Amostras[2].Valor[4][0]=new Double(0);
Amostras[2].Valor[5][0]=new Double(0);
Amostras[2].Valor[6][0]=new Double(0);
Amostras[2].Valor[7][0]=new Double(0);
Amostras[2].Valor[8][0]=new Double(0);
Amostras[2].Valor[9][0]=new Double(0);
Amostras[2].Valor[10][0]=new Double(0);
Amostras[2].Valor[0][1]=new Double(0);
Amostras[2].Valor[1][1]=new Double(0);
Amostras[2].Valor[2][1]=new Double(1);
Amostras[2].Valor[3][1]=new Double(1);
Amostras[2].Valor[4][1]=new Double(1);
Amostras[2].Valor[5][1]=new Double(1);
Amostras[2].Valor[6][1]=new Double(1);
Amostras[2].Valor[7][1]=new Double(1);
Amostras[2].Valor[8][1]=new Double(1);
Amostras[2].Valor[9][1]=new Double(0);
Amostras[2].Valor[10][1]=new Double(0);
Amostras[2].Valor[0][2]=new Double(0);
Amostras[2].Valor[1][2]=new Double(0);
Amostras[2].Valor[2][2]=new Double(1);
Amostras[2].Valor[3][2]=new Double(1);
Amostras[2].Valor[4][2]=new Double(1);
Amostras[2].Valor[5][2]=new Double(1);
Amostras[2].Valor[6][2]=new Double(1);
Amostras[2].Valor[7][2]=new Double(1);
Amostras[2].Valor[8][2]=new Double(1);
Amostras[2].Valor[9][2]=new Double(0);
Amostras[2].Valor[10][2]=new Double(0);
Amostras[2].Valor[0][3]=new Double(0);
Amostras[2].Valor[1][3]=new Double(0);
Amostras[2].Valor[2][3]=new Double(0);
Amostras[2].Valor[3][3]=new Double(0);
Amostras[2].Valor[4][3]=new Double(0);
Amostras[2].Valor[5][3]=new Double(0);
Amostras[2].Valor[6][3]=new Double(1);
Amostras[2].Valor[7][3]=new Double(1);
Amostras[2].Valor[8][3]=new Double(1);
Amostras[2].Valor[9][3]=new Double(0);
Amostras[2].Valor[10][3]=new Double(0);
Amostras[2].Valor[0][4]=new Double(0);
Amostras[2].Valor[1][4]=new Double(0);
Amostras[2].Valor[2][4]=new Double(1);
Amostras[2].Valor[3][4]=new Double(1);
Amostras[2].Valor[4][4]=new Double(1);
Amostras[2].Valor[5][4]=new Double(1);
Amostras[2].Valor[6][4]=new Double(1);
Amostras[2].Valor[7][4]=new Double(1);
Amostras[2].Valor[8][4]=new Double(1);
Amostras[2].Valor[9][4]=new Double(0);
Amostras[2].Valor[10][4]=new Double(0);
Amostras[2].Valor[0][5]=new Double(0);
Amostras[2].Valor[1][5]=new Double(0);
Amostras[2].Valor[2][5]=new Double(1);
Amostras[2].Valor[3][5]=new Double(1);
Amostras[2].Valor[4][5]=new Double(1);
Amostras[2].Valor[5][5]=new Double(0);
Amostras[2].Valor[6][5]=new Double(0);
Amostras[2].Valor[7][5]=new Double(0);
Amostras[2].Valor[8][5]=new Double(0);
Amostras[2].Valor[9][5]=new Double(0);
Amostras[2].Valor[10][5]=new Double(0);
Amostras[2].Valor[0][6]=new Double(0);
Amostras[2].Valor[1][6]=new Double(0);
Amostras[2].Valor[2][6]=new Double(1);
Amostras[2].Valor[3][6]=new Double(1);
Amostras[2].Valor[4][6]=new Double(1);
Amostras[2].Valor[5][6]=new Double(1);
Amostras[2].Valor[6][6]=new Double(1);
Amostras[2].Valor[7][6]=new Double(1);
Amostras[2].Valor[8][6]=new Double(1);
Amostras[2].Valor[9][6]=new Double(0);
Amostras[2].Valor[10][6]=new Double(0);
Amostras[2].Valor[0][7]=new Double(0);
Amostras[2].Valor[1][7]=new Double(0);
Amostras[2].Valor[2][7]=new Double(0);
Amostras[2].Valor[3][7]=new Double(0);
Amostras[2].Valor[4][7]=new Double(0);
Amostras[2].Valor[5][7]=new Double(0);
Amostras[2].Valor[6][7]=new Double(0);
Amostras[2].Valor[7][7]=new Double(0);
Amostras[2].Valor[8][7]=new Double(0);
Amostras[2].Valor[9][7]=new Double(0);
Amostras[2].Valor[10][7]=new Double(0);
 
Amostras[3].Valor[0][0]=new Double(0);
Amostras[3].Valor[1][0]=new Double(0);
Amostras[3].Valor[2][0]=new Double(0);
Amostras[3].Valor[3][0]=new Double(0);
Amostras[3].Valor[4][0]=new Double(0);
Amostras[3].Valor[5][0]=new Double(0);
Amostras[3].Valor[6][0]=new Double(0);
Amostras[3].Valor[7][0]=new Double(0);
Amostras[3].Valor[8][0]=new Double(0);
Amostras[3].Valor[9][0]=new Double(0);
Amostras[3].Valor[10][0]=new Double(0);
Amostras[3].Valor[0][1]=new Double(0);
Amostras[3].Valor[1][1]=new Double(0);
Amostras[3].Valor[2][1]=new Double(1);
Amostras[3].Valor[3][1]=new Double(1);
Amostras[3].Valor[4][1]=new Double(1);
Amostras[3].Valor[5][1]=new Double(1);
Amostras[3].Valor[6][1]=new Double(1);
Amostras[3].Valor[7][1]=new Double(1);
Amostras[3].Valor[8][1]=new Double(1);
Amostras[3].Valor[9][1]=new Double(0);
Amostras[3].Valor[10][1]=new Double(0);
Amostras[3].Valor[0][2]=new Double(0);
Amostras[3].Valor[1][2]=new Double(0);
Amostras[3].Valor[2][2]=new Double(0);
Amostras[3].Valor[3][2]=new Double(0);
Amostras[3].Valor[4][2]=new Double(0);
Amostras[3].Valor[5][2]=new Double(0);
Amostras[3].Valor[6][2]=new Double(1);
Amostras[3].Valor[7][2]=new Double(1);
Amostras[3].Valor[8][2]=new Double(1);
Amostras[3].Valor[9][2]=new Double(0);
Amostras[3].Valor[10][2]=new Double(0);
Amostras[3].Valor[0][3]=new Double(0);
Amostras[3].Valor[1][3]=new Double(0);
Amostras[3].Valor[2][3]=new Double(0);
Amostras[3].Valor[3][3]=new Double(0);
Amostras[3].Valor[4][3]=new Double(1);
Amostras[3].Valor[5][3]=new Double(1);
Amostras[3].Valor[6][3]=new Double(1);
Amostras[3].Valor[7][3]=new Double(1);
Amostras[3].Valor[8][3]=new Double(1);
Amostras[3].Valor[9][3]=new Double(0);
Amostras[3].Valor[10][3]=new Double(0);
Amostras[3].Valor[0][4]=new Double(0);
Amostras[3].Valor[1][4]=new Double(0);
Amostras[3].Valor[2][4]=new Double(0);
Amostras[3].Valor[3][4]=new Double(0);
Amostras[3].Valor[4][4]=new Double(1);
Amostras[3].Valor[5][4]=new Double(1);
Amostras[3].Valor[6][4]=new Double(1);
Amostras[3].Valor[7][4]=new Double(1);
Amostras[3].Valor[8][4]=new Double(1);
Amostras[3].Valor[9][4]=new Double(0);
Amostras[3].Valor[10][4]=new Double(0);
Amostras[3].Valor[0][5]=new Double(0);
Amostras[3].Valor[1][5]=new Double(0);
Amostras[3].Valor[2][5]=new Double(0);
Amostras[3].Valor[3][5]=new Double(0);
Amostras[3].Valor[4][5]=new Double(0);
Amostras[3].Valor[5][5]=new Double(0);
Amostras[3].Valor[6][5]=new Double(1);
Amostras[3].Valor[7][5]=new Double(1);
Amostras[3].Valor[8][5]=new Double(1);
Amostras[3].Valor[9][5]=new Double(0);
Amostras[3].Valor[10][5]=new Double(0);
Amostras[3].Valor[0][6]=new Double(0);
Amostras[3].Valor[1][6]=new Double(0);
Amostras[3].Valor[2][6]=new Double(1);
Amostras[3].Valor[3][6]=new Double(1);
Amostras[3].Valor[4][6]=new Double(1);
Amostras[3].Valor[5][6]=new Double(1);
Amostras[3].Valor[6][6]=new Double(1);
Amostras[3].Valor[7][6]=new Double(1);
Amostras[3].Valor[8][6]=new Double(1);
Amostras[3].Valor[9][6]=new Double(0);
Amostras[3].Valor[10][6]=new Double(0);
Amostras[3].Valor[0][7]=new Double(0);
Amostras[3].Valor[1][7]=new Double(0);
Amostras[3].Valor[2][7]=new Double(0);
Amostras[3].Valor[3][7]=new Double(0);
Amostras[3].Valor[4][7]=new Double(0);
Amostras[3].Valor[5][7]=new Double(0);
Amostras[3].Valor[6][7]=new Double(0);
Amostras[3].Valor[7][7]=new Double(0);
Amostras[3].Valor[8][7]=new Double(0);
Amostras[3].Valor[9][7]=new Double(0);
Amostras[3].Valor[10][7]=new Double(0);
 
Amostras[4].Valor[0][0]=new Double(0);
Amostras[4].Valor[1][0]=new Double(0);
Amostras[4].Valor[2][0]=new Double(0);
Amostras[4].Valor[3][0]=new Double(0);
Amostras[4].Valor[4][0]=new Double(0);
Amostras[4].Valor[5][0]=new Double(0);
Amostras[4].Valor[6][0]=new Double(0);
Amostras[4].Valor[7][0]=new Double(0);
Amostras[4].Valor[8][0]=new Double(0);
Amostras[4].Valor[9][0]=new Double(0);
Amostras[4].Valor[10][0]=new Double(0);
Amostras[4].Valor[0][1]=new Double(0);
Amostras[4].Valor[1][1]=new Double(0);
Amostras[4].Valor[2][1]=new Double(1);
Amostras[4].Valor[3][1]=new Double(1);
Amostras[4].Valor[4][1]=new Double(0);
Amostras[4].Valor[5][1]=new Double(0);
Amostras[4].Valor[6][1]=new Double(1);
Amostras[4].Valor[7][1]=new Double(1);
Amostras[4].Valor[8][1]=new Double(1);
Amostras[4].Valor[9][1]=new Double(0);
Amostras[4].Valor[10][1]=new Double(0);
Amostras[4].Valor[0][2]=new Double(0);
Amostras[4].Valor[1][2]=new Double(0);
Amostras[4].Valor[2][2]=new Double(1);
Amostras[4].Valor[3][2]=new Double(1);
Amostras[4].Valor[4][2]=new Double(0);
Amostras[4].Valor[5][2]=new Double(0);
Amostras[4].Valor[6][2]=new Double(1);
Amostras[4].Valor[7][2]=new Double(1);
Amostras[4].Valor[8][2]=new Double(1);
Amostras[4].Valor[9][2]=new Double(0);
Amostras[4].Valor[10][2]=new Double(0);
Amostras[4].Valor[0][3]=new Double(0);
Amostras[4].Valor[1][3]=new Double(0);
Amostras[4].Valor[2][3]=new Double(1);
Amostras[4].Valor[3][3]=new Double(1);
Amostras[4].Valor[4][3]=new Double(0);
Amostras[4].Valor[5][3]=new Double(0);
Amostras[4].Valor[6][3]=new Double(1);
Amostras[4].Valor[7][3]=new Double(1);
Amostras[4].Valor[8][3]=new Double(1);
Amostras[4].Valor[9][3]=new Double(0);
Amostras[4].Valor[10][3]=new Double(0);
Amostras[4].Valor[0][4]=new Double(0);
Amostras[4].Valor[1][4]=new Double(0);
Amostras[4].Valor[2][4]=new Double(1);
Amostras[4].Valor[3][4]=new Double(1);
Amostras[4].Valor[4][4]=new Double(1);
Amostras[4].Valor[5][4]=new Double(1);
Amostras[4].Valor[6][4]=new Double(1);
Amostras[4].Valor[7][4]=new Double(1);
Amostras[4].Valor[8][4]=new Double(1);
Amostras[4].Valor[9][4]=new Double(0);
Amostras[4].Valor[10][4]=new Double(0);
Amostras[4].Valor[0][5]=new Double(0);
Amostras[4].Valor[1][5]=new Double(0);
Amostras[4].Valor[2][5]=new Double(0);
Amostras[4].Valor[3][5]=new Double(0);
Amostras[4].Valor[4][5]=new Double(0);
Amostras[4].Valor[5][5]=new Double(0);
Amostras[4].Valor[6][5]=new Double(1);
Amostras[4].Valor[7][5]=new Double(1);
Amostras[4].Valor[8][5]=new Double(1);
Amostras[4].Valor[9][5]=new Double(0);
Amostras[4].Valor[10][5]=new Double(0);
Amostras[4].Valor[0][6]=new Double(0);
Amostras[4].Valor[1][6]=new Double(0);
Amostras[4].Valor[2][6]=new Double(0);
Amostras[4].Valor[3][6]=new Double(0);
Amostras[4].Valor[4][6]=new Double(0);
Amostras[4].Valor[5][6]=new Double(0);
Amostras[4].Valor[6][6]=new Double(1);
Amostras[4].Valor[7][6]=new Double(1);
Amostras[4].Valor[8][6]=new Double(1);
Amostras[4].Valor[9][6]=new Double(0);
Amostras[4].Valor[10][6]=new Double(0);
Amostras[4].Valor[0][7]=new Double(0);
Amostras[4].Valor[1][7]=new Double(0);
Amostras[4].Valor[2][7]=new Double(0);
Amostras[4].Valor[3][7]=new Double(0);
Amostras[4].Valor[4][7]=new Double(0);
Amostras[4].Valor[5][7]=new Double(0);
Amostras[4].Valor[6][7]=new Double(0);
Amostras[4].Valor[7][7]=new Double(0);
Amostras[4].Valor[8][7]=new Double(0);
Amostras[4].Valor[9][7]=new Double(0);
Amostras[4].Valor[10][7]=new Double(0);

AmostraTeste.Valor[0][0]=new Double(0);
AmostraTeste.Valor[1][0]=new Double(0);
AmostraTeste.Valor[2][0]=new Double(0);
AmostraTeste.Valor[3][0]=new Double(0);
AmostraTeste.Valor[4][0]=new Double(0);
AmostraTeste.Valor[5][0]=new Double(0);
AmostraTeste.Valor[6][0]=new Double(0);
AmostraTeste.Valor[7][0]=new Double(0);
AmostraTeste.Valor[8][0]=new Double(0);
AmostraTeste.Valor[9][0]=new Double(0);
AmostraTeste.Valor[10][0]=new Double(0);
AmostraTeste.Valor[0][1]=new Double(0);
AmostraTeste.Valor[1][1]=new Double(0);
AmostraTeste.Valor[2][1]=new Double(1);
AmostraTeste.Valor[3][1]=new Double(1);
AmostraTeste.Valor[4][1]=new Double(1);
AmostraTeste.Valor[5][1]=new Double(1);
AmostraTeste.Valor[6][1]=new Double(1);
AmostraTeste.Valor[7][1]=new Double(1);
AmostraTeste.Valor[8][1]=new Double(1);
AmostraTeste.Valor[9][1]=new Double(0);
AmostraTeste.Valor[10][1]=new Double(0);
AmostraTeste.Valor[0][2]=new Double(0);
AmostraTeste.Valor[1][2]=new Double(0);
AmostraTeste.Valor[2][2]=new Double(0);
AmostraTeste.Valor[3][2]=new Double(0);
AmostraTeste.Valor[4][2]=new Double(0);
AmostraTeste.Valor[5][2]=new Double(0);
AmostraTeste.Valor[6][2]=new Double(1);
AmostraTeste.Valor[7][2]=new Double(1);
AmostraTeste.Valor[8][2]=new Double(1);
AmostraTeste.Valor[9][2]=new Double(0);
AmostraTeste.Valor[10][2]=new Double(0);
AmostraTeste.Valor[0][3]=new Double(0);
AmostraTeste.Valor[1][3]=new Double(0);
AmostraTeste.Valor[2][3]=new Double(0);
AmostraTeste.Valor[3][3]=new Double(0);
AmostraTeste.Valor[4][3]=new Double(1);
AmostraTeste.Valor[5][3]=new Double(1);
AmostraTeste.Valor[6][3]=new Double(1);
AmostraTeste.Valor[7][3]=new Double(1);
AmostraTeste.Valor[8][3]=new Double(1);
AmostraTeste.Valor[9][3]=new Double(0);
AmostraTeste.Valor[10][3]=new Double(0);
AmostraTeste.Valor[0][4]=new Double(0);
AmostraTeste.Valor[1][4]=new Double(0);
AmostraTeste.Valor[2][4]=new Double(0);
AmostraTeste.Valor[3][4]=new Double(0);
AmostraTeste.Valor[4][4]=new Double(1);
AmostraTeste.Valor[5][4]=new Double(1);
AmostraTeste.Valor[6][4]=new Double(1);
AmostraTeste.Valor[7][4]=new Double(1);
AmostraTeste.Valor[8][4]=new Double(1);
AmostraTeste.Valor[9][4]=new Double(0);
AmostraTeste.Valor[10][4]=new Double(0);
AmostraTeste.Valor[0][5]=new Double(0);
AmostraTeste.Valor[1][5]=new Double(0);
AmostraTeste.Valor[2][5]=new Double(0);
AmostraTeste.Valor[3][5]=new Double(0);
AmostraTeste.Valor[4][5]=new Double(0);
AmostraTeste.Valor[5][5]=new Double(0);
AmostraTeste.Valor[6][5]=new Double(1);
AmostraTeste.Valor[7][5]=new Double(1);
AmostraTeste.Valor[8][5]=new Double(1);
AmostraTeste.Valor[9][5]=new Double(0);
AmostraTeste.Valor[10][5]=new Double(0);
AmostraTeste.Valor[0][6]=new Double(0);
AmostraTeste.Valor[1][6]=new Double(0);
AmostraTeste.Valor[2][6]=new Double(1);
AmostraTeste.Valor[3][6]=new Double(1);
AmostraTeste.Valor[4][6]=new Double(1);
AmostraTeste.Valor[5][6]=new Double(1);
AmostraTeste.Valor[6][6]=new Double(1);
AmostraTeste.Valor[7][6]=new Double(1);
AmostraTeste.Valor[8][6]=new Double(1);
AmostraTeste.Valor[9][6]=new Double(0);
AmostraTeste.Valor[10][6]=new Double(0);
AmostraTeste.Valor[0][7]=new Double(0);
AmostraTeste.Valor[1][7]=new Double(0);
AmostraTeste.Valor[2][7]=new Double(0);
AmostraTeste.Valor[3][7]=new Double(0);
AmostraTeste.Valor[4][7]=new Double(0);
AmostraTeste.Valor[5][7]=new Double(0);
AmostraTeste.Valor[6][7]=new Double(0);
AmostraTeste.Valor[7][7]=new Double(0);
AmostraTeste.Valor[8][7]=new Double(0);
AmostraTeste.Valor[9][7]=new Double(0);
AmostraTeste.Valor[10][7]=new Double(0);

        
		try{			
			Thread.sleep(100);
		}catch(Exception e){
			
		}
		
		SaidaEsperada=new double[5][5];
		
		for(f=0;f<5;f++)
		    for(g=0;g<5;g++)
		        SaidaEsperada[f][g]=0;		
				
		SaidaEsperada[0][0]=1;
		SaidaEsperada[1][1]=1;
		SaidaEsperada[2][2]=1;
		SaidaEsperada[3][3]=1;
		SaidaEsperada[4][4]=1;
		
		theNeuralNetwork.Carregar(Amostras,SaidaEsperada);
		theNeuralNetwork.Aprender();
		theNeuralNetwork.Testar(AmostraTeste);		
		
		while(true){
			try{
				Thread.sleep(100);				
			}catch(Exception e){
				System.out.println(e);
			}
		}
	}
	
	
	
}