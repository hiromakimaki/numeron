import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Numeron{
	public static void main(String[] args){
		NumeronGameMaster master = new NumeronGameMaster(new Hand(1,4,7), new SimpleStrategy());
		master.playGame();
	}
}

interface Strategy{

	public Hand choiceHand(List<Hand> hands);

}


class SimpleStrategy implements Strategy{

	public Hand choiceHand(List<Hand> hands) {
		// Head
		return hands.get(0);
	}
}


class NumeronGameMaster{

	private List<Hand> hands;
	private Hand answer;
	private Strategy strategy;

	NumeronGameMaster(Hand answer, Strategy strategy){
		this.hands = Hand.getAllHands();
		this.answer = answer;
		this.strategy = strategy;
	}

	private List<Hand> getCandidates(List<Hand> hands, Trial trial){
		return hands
				.stream()
				.filter(h -> trial.getResponse().equals(h.getHitAndBlow(trial.getHand())))
				.collect(Collectors.toList());
	}

	public void playGame() {
		List<Trial> trialList = new ArrayList<Trial>();
		while (this.hands.size() > 0) {
			Hand h = this.strategy.choiceHand(this.hands);
			this.hands.remove(h);
			Trial trial = new Trial(h, this.answer.getHitAndBlow(h));
			trialList.add(trial);
			this.hands = this.getCandidates(this.hands, trial);
		}
		System.out.println(trialList);
	}
}


class Hand{

	private Integer left;
	private Integer center;
	private Integer right;

	Hand(Integer l, Integer c, Integer r){
		if (l == c || l == r || c == r) {
			throw new IllegalArgumentException("3 arguments must be different each other.");
		}
		this.left = l;
		this.center = c;
		this.right = r;
	}

	public static List<Hand> getAllHands(){
		List<Integer> baseNumList = Arrays.asList(0,1,2,3,4,5,6,7,8,9);
		return baseNumList
				.stream()
				.flatMap(x ->
					baseNumList
					.stream()
					.flatMap(y -> baseNumList
						.stream()
						.filter(z -> x != y && x != z && y != z)
						.map(z ->
							new Hand(x,y,z)
						)
					)
				)
				.collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "(" + this.left + "," + this.center + "," + this.right + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Hand) {
			Hand h = (Hand) o;
			return this.left == h.getLeft() && this.center == h.getCenter() && this.right == h.getRight();
		} else {
			return false;
		}
	}

	public Integer countHit(Hand h){
		return Integer.valueOf(
				(int) Arrays.asList(h.getLeft() == this.getLeft(), h.getCenter() == this.getCenter(), h.getRight() == this.getRight())
				.stream()
				.filter(x -> x)
				.count());
	}

	public Integer countBlow(Hand h){
		return Integer.valueOf((int) Arrays.asList(h.getLeft(), h.getCenter(), h.getRight())
				.stream()
				.filter(x -> (x == this.getLeft() || x == this.getCenter() || x == this.getRight()))
				.count())
				- this.countHit(h);
	}

	public Response getHitAndBlow(Hand h) {
		return new Response(this.countHit(h), this.countBlow(h));
	}

	public Integer getLeft(){
		return this.left;
	}

	public Integer getCenter(){
		return this.center;
	}

	public Integer getRight(){
		return this.right;
	}
}


class Response{

	private Integer hit;
	private Integer blow;

	Response(Integer hit, Integer blow){
		this.hit = hit;
		this.blow = blow;
	}

	@Override
	public String toString() {
		return "(Hit: " + this.hit + ", Blow: " + this.blow + ")";
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Response) {
			Response r = (Response) o;
			return this.hit == r.getHit() && this.blow == r.getBlow();
		} else {
			return false;
		}
	}

	public Integer getHit(){
		return this.hit;
	}

	public Integer getBlow(){
		return this.blow;
	}
}


class Trial{
	private Hand hand;
	private Response response;

	Trial(Hand hand, Response response){
		this.hand = hand;
		this.response = response;
	}

	@Override
	public String toString() {
		return getClass().toString() + "(" + this.hand.toString() + ", " + this.response.toString() + ")";
	}

	public Hand getHand(){
		return this.hand;
	}

	public Response getResponse(){
		return this.response;
	}

	public void setHand(Hand hand){
		this.hand = hand;
	}

	public void setResponse(Response response){
		this.response = response;
	}
}