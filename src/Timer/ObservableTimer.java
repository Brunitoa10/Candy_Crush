package Timer;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import javax.swing.SwingUtilities;

/**
 * Timer observable (patrón Observer) que emite un TickEvent por segundo.
 *
 * Modelo de hilos: el scheduler solo "cuenta" el tiempo; la notificación a los observers se
 * despacha en el hilo de Swing (EDT). Así toda la lógica del juego (reloj, bombas, tablero y GUI)
 * corre en un único hilo y no hay condiciones de carrera con las acciones del jugador.
 */
public class ObservableTimer {

	private static final long PERIODO_SEGUNDOS = 1;

	private final ScheduledExecutorService scheduler;
	private final Executor despachador;
	private final List<TickObserver> observers = new CopyOnWriteArrayList<>();
	private ScheduledFuture<?> future;
	private volatile boolean pausado;

	public ObservableTimer() {
		this(SwingUtilities::invokeLater);
	}

	/** @param despachador dónde se ejecutan las notificaciones (por defecto, el hilo de Swing). */
	public ObservableTimer(Executor despachador) {
		this.despachador = despachador;
		scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
			Thread hilo = new Thread(r, "timer-juego");
			hilo.setDaemon(true);
			return hilo;
		});
		programar();
	}

	public void addObserver(TickObserver observer) {
		if (!observers.contains(observer)) {
			observers.add(observer);
		}
	}

	public void desuscribirse(TickObserver observer) {
		observers.remove(observer);
	}

	public void desuscribirTodo() {
		observers.clear();
	}

	private void tick() {
		despachador.execute(() -> {
			if (pausado) {
				return; // un tick que quedó encolado justo antes de pausar se descarta
			}
			TickEvent evento = new TickEvent();
			for (TickObserver observer : observers) {
				try {
					observer.update(evento);
				} catch (RuntimeException e) {
					e.printStackTrace();
				}
			}
		});
	}

	public synchronized void pause() {
		pausado = true;
		if (future != null && !future.isCancelled()) {
			future.cancel(false);
		}
	}

	public synchronized void resume() {
		pausado = false;
		if (future == null || future.isCancelled()) {
			programar();
		}
	}

	public void stop() {
		scheduler.shutdown();
	}

	private synchronized void programar() {
		future = scheduler.scheduleAtFixedRate(this::tick, PERIODO_SEGUNDOS, PERIODO_SEGUNDOS, TimeUnit.SECONDS);
	}
}
